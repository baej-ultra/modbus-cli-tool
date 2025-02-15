package org.baej.modbusclitool.modbus;

import com.digitalpetri.modbus.client.ModbusClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.ReadHoldingRegistersRequest;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ModbusPollingService {

    private final ModbusConnectionManager connectionManager;
    private final ScheduledExecutorService executorService;
    private final ModbusClientConnectionParams connectionParams;
    private final Terminal terminal;

    private ModbusClient modbusClient;
    private boolean isPolling;

    public ModbusPollingService(ModbusConnectionManager connectionManager, ModbusClientConnectionParams connectionParams, Terminal terminal, Terminal terminal1) {
        this.connectionManager = connectionManager;
        this.connectionParams = connectionParams;
        this.terminal = terminal1;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startPolling() {
        if (!connectionManager.isConnected()) {
            throw new RuntimeException("Client not connected");
        }

        modbusClient = connectionManager.getModbusClient();

        executorService.scheduleWithFixedDelay(this::poll,
                0,
                connectionParams.getPollingInverval(),
                TimeUnit.MILLISECONDS);
        isPolling = true;
    }

    public void stopPolling() {
        if (!executorService.isShutdown()) {
            isPolling = false;
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }

    public boolean isPolling() {
        return isPolling;
    }

    private void poll() {
        var req = new ReadHoldingRegistersRequest(1, 10);
        try {
            var response  = modbusClient.readHoldingRegisters(1, req);
            byte[] registers = response.registers();
            var register = ByteBuffer.wrap(registers).getFloat(0);
            terminal.writer().println(register);
            terminal.flush();
        } catch (ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e) {
            System.out.println(e.getMessage());
        }
    }
}
