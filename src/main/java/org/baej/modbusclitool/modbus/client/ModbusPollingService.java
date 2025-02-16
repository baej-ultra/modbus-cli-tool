package org.baej.modbusclitool.modbus.client;

import com.digitalpetri.modbus.client.ModbusClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.ReadHoldingRegistersRequest;
import org.baej.modbusclitool.modbus.core.ModbusData;
import org.baej.modbusclitool.modbus.core.ModbusObservable;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ModbusPollingService extends ModbusObservable {

    private final ModbusConnectionManager connectionManager;
    private final ModbusClientPollingParams pollingParams;

    private final ScheduledExecutorService executorService;
    private ModbusClient modbusClient;
    private boolean isPolling;
    private ScheduledFuture<?> scheduledFuture;

    public ModbusPollingService(ModbusConnectionManager connectionManager, ModbusClientPollingParams pollingParams) {
        this.connectionManager = connectionManager;
        this.pollingParams = pollingParams;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startPolling() {
        if (!connectionManager.isConnected()) {
            throw new RuntimeException("Client not connected");
        }

        modbusClient = connectionManager.getModbusClient();

        scheduledFuture = executorService.scheduleWithFixedDelay(this::poll, 0,
                pollingParams.getPollingInterval(), TimeUnit.MILLISECONDS);
        isPolling = true;
    }

    public void stopPolling() {
        isPolling = false;
        scheduledFuture.cancel(false);
    }

    public boolean isPolling() {
        return isPolling;
    }

    private void poll() {
        var req = new ReadHoldingRegistersRequest(1, 10);
        try {
            var response = modbusClient.readHoldingRegisters(1, req);
            byte[] registers = response.registers();

            ModbusData modbusData = new ModbusData(registers, pollingParams.getDataFormat(),
                    pollingParams.getByteOrder());

            notifyObservers(modbusData);

        } catch (ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e) {
            System.out.println(e.getMessage());
        }
    }
}
