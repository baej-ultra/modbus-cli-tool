package org.baej.modbusclitool.modbus;

import com.digitalpetri.modbus.client.ModbusClient;
import com.digitalpetri.modbus.client.ModbusTcpClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.tcp.client.NettyTcpClientTransport;
import org.springframework.stereotype.Component;

@Component
public class ModbusConnectionManager {

    private ModbusClient modbusClient;
    private final ModbusClientConnectionParams connectionParams;

    public ModbusConnectionManager(ModbusClientConnectionParams connectionParams) {
        this.connectionParams = connectionParams;
    }

    public void connect() {
        if (modbusClient == null || !modbusClient.isConnected()) {
            var transport = NettyTcpClientTransport.create(cfg -> {
                cfg.hostname = connectionParams.getHost();
                cfg.port = connectionParams.getPort();
            });

            modbusClient = ModbusTcpClient.create(transport);
            try {
                modbusClient.connect();
            } catch (ModbusExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void disconnect() {
        if (modbusClient != null && modbusClient.isConnected()) {
            try {
                modbusClient.disconnect();
            } catch (ModbusExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ModbusClient getModbusClient() {
        if (modbusClient == null || !modbusClient.isConnected()) {
            throw new RuntimeException("Modbus client not connected");
        }

        return modbusClient;
    }

    public boolean isConnected() {
        return (modbusClient != null) && modbusClient.isConnected();
    }

}
