package org.baej.modbusclitool.modbus.client;

import com.digitalpetri.modbus.client.ModbusClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.ReadHoldingRegistersRequest;
import jakarta.annotation.PostConstruct;
import org.baej.modbusclitool.modbus.ModbusTerminalDisplay;
import org.baej.modbusclitool.modbus.core.ModbusData;
import org.baej.modbusclitool.modbus.core.ModbusDataObservable;
import org.springframework.shell.table.Table;
import org.springframework.stereotype.Service;

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

    public ModbusDataPollingService(ModbusConnectionManager connectionManager,
                                    ModbusClientPollingParams pollingParams,
                                    ModbusTerminalDisplay modbusTerminalDisplay) {
        this.connectionManager = connectionManager;
        this.pollingParams = pollingParams;
        this.modbusTerminalDisplay = modbusTerminalDisplay;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    private void init() {
        this.subscribe(modbusTerminalDisplay);
    }

    public void startPolling(int interval) {
        if (!connectionManager.isConnected()) {
            throw new RuntimeException("Client not connected");
        }

        scheduledFuture = executorService.scheduleWithFixedDelay(this::poll, 0,
                interval, TimeUnit.MILLISECONDS);
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
        ModbusClient modbusClient = connectionManager.getModbusClient();
        Table table = null;
        var req = new ReadHoldingRegistersRequest(pollingParams.getStartingAddress(), pollingParams.getQuantity());
        try {
            var response = modbusClient.readHoldingRegisters(pollingParams.getUnitId(), req);
            byte[] registers = response.registers();

            ModbusData modbusData = new ModbusData(registers, pollingParams.getDataFormat(),
                    pollingParams.getByteOrder(), pollingParams.isByteSwap());

            notifyObservers(modbusData);
        } catch (ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
