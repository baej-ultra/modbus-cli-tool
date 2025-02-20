package org.baej.modbusclitool.modbus.server;

import com.digitalpetri.modbus.server.ModbusTcpServer;
import com.digitalpetri.modbus.tcp.server.NettyTcpServerTransport;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class ModbusServerConnectionManager {

    private final ModbusServerService modbusServerService;
    private ModbusTcpServer modbusServer;

    public ModbusServerConnectionManager(ModbusServerService modbusServerService) {
        this.modbusServerService = modbusServerService;
    }

    public void start(int port) {
        if (modbusServer == null) {

            var transport = NettyTcpServerTransport.create(cfg -> cfg.port = port);

            modbusServer = ModbusTcpServer.create(transport, modbusServerService);
            try {
                modbusServer.start();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        if (modbusServer != null) {
            try {
                modbusServer.stop();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ModbusTcpServer getModbusServer() {
        return modbusServer;
    }
}
