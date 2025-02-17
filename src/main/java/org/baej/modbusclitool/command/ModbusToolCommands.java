package org.baej.modbusclitool.command;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.baej.modbusclitool.exception.InvalidModbusConnectionParameterException;
import org.baej.modbusclitool.modbus.client.*;
import org.baej.modbusclitool.modbus.core.ModbusDataByteOrder;
import org.baej.modbusclitool.modbus.core.ModbusDataFormat;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@ShellComponent
public class ModbusToolCommands {

    private final ModbusConnectionManager connectionManager;
    private final ModbusDataPollingService pollingService;
    private final ModbusClientConnectionParams connectionParams;
    private final ComponentFlow.Builder componentFlowBuilder;
    private final Terminal terminal;
    private final ModbusClientPollingParams modbusClientPollingParams;
    public volatile int i = 0;

    public ModbusToolCommands(ModbusConnectionManager connectionManager,
                              ModbusDataPollingService pollingService,
                              ModbusClientConnectionParams connectionParams,
                              ComponentFlow.Builder componentFlowBuilder, Terminal terminal, ModbusClientPollingParams modbusClientPollingParams) {
        this.connectionManager = connectionManager;
        this.pollingService = pollingService;
        this.connectionParams = connectionParams;
        this.componentFlowBuilder = componentFlowBuilder;
        this.terminal = terminal;
        this.modbusClientPollingParams = modbusClientPollingParams;
    }

    @ShellMethod(value = "Set modbus connection parameters")
    public void connectionParameters() {
        runConnectionParametersFlow();
    }

    @ShellMethod(value = "Connect to ModbusTCP Server")
    public String connect() {
        connectionManager.connect();
        return "Connected to " + connectionParams.getHost();
    }

    @ShellMethod(value = "Poll")
    public void poll(@ShellOption(defaultValue = "0") int interval) throws IOException {
        if (interval == 0) {
            pollingService.poll();
        } else {
            pollingService.startPolling(interval);
            TerminalUI ui = new TerminalUI(terminal);
            EventLoop eventLoop = ui.getEventLoop();
            eventLoop.keyEvents()
                    .subscribe(event -> {
                        if (event.getPlainKey() == KeyEvent.Key.q && event.hasCtrl()) {
                            pollingService.stopPolling();
                        }
                    });
        }
    }

    @ShellMethod(value = "Poll settings")
    public void pollSettings() {
        runPollingParametersFlow();
    }

    private void runConnectionParametersFlow() {
        InetAddressValidator inetValidator = InetAddressValidator.getInstance();

        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withStringInput("host")
                .name("IP address")
                .defaultValue(connectionParams.getHost())
                .and()
                .withStringInput("port")
                .name("Port")
                .defaultValue(Integer.toString(connectionParams.getPort()))
                .and()
                .withStringInput("timeout")
                .name("Connection timeout")
                .defaultValue(Integer.toString(connectionParams.getTimeout()))
                .and()
                .build();

        var result = flow
                .run()
                .getContext();

        // get values from the flow
        String host = result.get("host");
        int port = Integer.parseInt(result.get("port"));
        int timeout = Integer.parseInt(result.get("timeout"));

        if (!inetValidator.isValid(host)) {
            throw new InvalidModbusConnectionParameterException("Invalid address");
        }

        if (port < 0 || port > 65535) {
            throw new InvalidModbusConnectionParameterException("Invalid port");
        }

        if (timeout < 0) {
            throw new InvalidModbusConnectionParameterException("Invalid timeout");
        }

        connectionParams.setHost(host);
        connectionParams.setPort(port);
        connectionParams.setTimeout(timeout);
    }

    private void runPollingParametersFlow() {
        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withStringInput("id")
                .name("Unit ID")
                .defaultValue(Integer.toString(modbusClientPollingParams.getUnitId()))
                .and()
                .withStringInput("start")
                .name("Starting address")
                .defaultValue(Integer.toString(modbusClientPollingParams.getStartingAddress()))
                .and()
                .withStringInput("quantity")
                .name("Quantity")
                .defaultValue(Integer.toString(modbusClientPollingParams.getQuantity()))
                .and()
                .withSingleItemSelector("format")
                .name("Format")
                .selectItems(List.of(
                        SelectItem.of("Int16", "SHORT_INT"),
                        SelectItem.of("Int32", "INT"),
                        SelectItem.of("Int64", "LONG_INT"),
                        SelectItem.of("Float", "FLOAT"),
                        SelectItem.of("Double", "DOUBLE")
                ))
                .and()
                .withSingleItemSelector("endian")
                .name("Endianness")
                .selectItems(List.of(
                        SelectItem.of("Big-endian", "BIG_ENDIAN"),
                        SelectItem.of("Small-endian", "SMALL_ENDIAN")
                ))
                .and()
                .withSingleItemSelector("swap")
                .name("Byte swap")
                .selectItems(Map.of(
                        "NO", "no",
                        "YES", "yes"
                ))
                .and()
                .build();

        var result = flow
                .run()
                .getContext();
        modbusClientPollingParams.setUnitId(Integer.parseInt(result.get("id")));
        modbusClientPollingParams.setStartingAddress(Integer.parseInt(result.get("start")));
        modbusClientPollingParams.setQuantity(Integer.parseInt(result.get("quantity")));
        modbusClientPollingParams.setDataFormat(ModbusDataFormat.valueOf(result.get("format")));
        modbusClientPollingParams.setByteOrder(ModbusDataByteOrder.valueOf(result.get("endian")));
        modbusClientPollingParams.setByteSwap(result.get("endian").equals("yes"));
    }

}
