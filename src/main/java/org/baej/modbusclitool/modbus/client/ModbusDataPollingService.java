package org.baej.modbusclitool.modbus.client;

import com.digitalpetri.modbus.client.ModbusClient;
import jakarta.annotation.PostConstruct;
import org.baej.modbusclitool.modbus.ModbusTerminalDisplay;
import org.baej.modbusclitool.modbus.client.strategy.*;
import org.baej.modbusclitool.modbus.core.ModbusData;
import org.baej.modbusclitool.modbus.core.ModbusDataObservable;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ModbusDataPollingService extends ModbusDataObservable {

    private final ModbusConnectionManager connectionManager;
    private final ModbusClientPollingParams pollingParams;
    private final ScheduledExecutorService executorService;
    private final ModbusTerminalDisplay modbusTerminalDisplay;
    private boolean isPolling;
    private ScheduledFuture<?> scheduledFuture;

    private final Map<ModbusClientReadFunction, ModbusRequestStrategy>
            strategyMap = new EnumMap<>(ModbusClientReadFunction.class);

    public ModbusDataPollingService(ModbusConnectionManager connectionManager,
                                    ModbusClientPollingParams pollingParams,
                                    ModbusTerminalDisplay modbusTerminalDisplay) {
        this.connectionManager = connectionManager;
        this.pollingParams = pollingParams;
        this.modbusTerminalDisplay = modbusTerminalDisplay;
        executorService = Executors.newSingleThreadScheduledExecutor();

        // Register strategies
        strategyMap.put(ModbusClientReadFunction.INPUT_REGISTERS,
                new InputRegistersStrategy());
        strategyMap.put(ModbusClientReadFunction.HOLDING_REGISTERS,
                new HoldingRegistersStrategy());
        strategyMap.put(ModbusClientReadFunction.DISCRETE_INPUTS,
                new DiscreteInputsStrategy());
        strategyMap.put(ModbusClientReadFunction.COILS,
                new DiscreteCoilsStrategy());
    }

    @PostConstruct
    private void init() {
        this.subscribe(modbusTerminalDisplay);
    }

    public void startPolling(int interval) {
        if (!connectionManager.isConnected()) {
            throw new RuntimeException("Client not connected");
        }

        // Start polling
        scheduledFuture = executorService.scheduleWithFixedDelay(
                this::poll,
                0,
                interval,
                TimeUnit.MILLISECONDS
        );
        isPolling = true;
    }

    public void stopPolling() {
        isPolling = false;
        scheduledFuture.cancel(false);
    }

    public boolean isPolling() {
        return isPolling;
    }

    public void poll() {
        ModbusData modbusData = requestModbusData();
        notifyObservers(modbusData);
    }

    private ModbusData requestModbusData() {
        ModbusClient modbusClient = connectionManager.getModbusClient();
        var readFunction = pollingParams.getReadFunction();

        ModbusRequestStrategy strategy = strategyMap.get(readFunction);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Unsupported read function: " + readFunction
            );
        }
        return strategy.request(modbusClient, pollingParams);
    }
}
