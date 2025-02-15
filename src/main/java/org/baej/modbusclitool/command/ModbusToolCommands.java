package org.baej.modbusclitool.command;

import org.baej.modbusclitool.modbus.ModbusConnectionManager;
import org.baej.modbusclitool.modbus.ModbusPollingService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ModbusToolCommands {

    private final ModbusConnectionManager connectionManager;
    private final ModbusPollingService pollingService;

    public ModbusToolCommands(ModbusConnectionManager connectionManager, ModbusPollingService pollingService) {
        this.connectionManager = connectionManager;
        this.pollingService = pollingService;
    }

    @ShellMethod
    public void poll() {
        connectionManager.connect();
        pollingService.startPolling();
    }
}
