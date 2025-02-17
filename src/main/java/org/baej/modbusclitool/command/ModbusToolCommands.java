package org.baej.modbusclitool.command;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.baej.modbusclitool.exception.InvalidModbusConnectionParameterException;
import org.baej.modbusclitool.modbus.client.ModbusClientConnectionParams;
import org.baej.modbusclitool.modbus.client.ModbusClientPollingParams;
import org.baej.modbusclitool.modbus.client.ModbusConnectionManager;
import org.baej.modbusclitool.modbus.client.ModbusDataPollingService;
import org.baej.modbusclitool.modbus.core.ModbusDataByteOrder;
import org.baej.modbusclitool.modbus.core.ModbusDataDisplayFormat;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.baej.modbusclitool.modbus.client.ModbusClientReadFunction.*;
import static org.baej.modbusclitool.modbus.core.ModbusDataByteOrder.BIG_ENDIAN;
import static org.baej.modbusclitool.modbus.core.ModbusDataByteOrder.SMALL_ENDIAN;
import static org.baej.modbusclitool.modbus.core.ModbusDataDisplayFormat.*;

@ShellComponent
public class ModbusToolCommands {

    private final ModbusConnectionManager connectionManager;
    private final ModbusDataPollingService pollingService;
    private final ModbusClientConnectionParams connectionParams;
    private final ComponentFlow.Builder componentFlowBuilder;
    private final ModbusClientPollingParams modbusClientPollingParams;
    public volatile int i = 0;

    public ModbusToolCommands(ModbusConnectionManager connectionManager,
                              ModbusDataPollingService pollingService,
                              ModbusClientConnectionParams connectionParams,
                              ComponentFlow.Builder componentFlowBuilder,
                              ModbusClientPollingParams modbusClientPollingParams) {
        this.connectionManager = connectionManager;
        this.pollingService = pollingService;
        this.connectionParams = connectionParams;
        this.componentFlowBuilder = componentFlowBuilder;
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
        pollingService.poll();
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
                .withSingleItemSelector("fun")
                .name("Function")
                .selectItems(List.of(
                        SelectItem.of("01 Read coils", COILS.toString()),
                        SelectItem.of("02 Read discrete inputs", DISCRETE_INPUTS.toString()),
                        SelectItem.of("03 Read holding registers", HOLDING_REGISTERS.toString()),
                        SelectItem.of("04 Read input registers", INPUT_REGISTERS.toString())
                ))
                .and()
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
                .name("Display format")
                .selectItems(List.of(
                        SelectItem.of("Int16", SHORT_INT.toString()),
                        SelectItem.of("Int32", INT.toString()),
                        SelectItem.of("Int64", LONG_INT.toString()),
                        SelectItem.of("Float", FLOAT.toString()),
                        SelectItem.of("Double", DOUBLE.toString())
                ))
                .and()
                .withSingleItemSelector("endian")
                .name("Endianness")
                .selectItems(List.of(
                        SelectItem.of("Big-endian", BIG_ENDIAN.toString()),
                        SelectItem.of("Small-endian", SMALL_ENDIAN.toString())
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
        modbusClientPollingParams.setDataFormat(ModbusDataDisplayFormat.valueOf(result.get("format")));
        modbusClientPollingParams.setByteOrder(ModbusDataByteOrder.valueOf(result.get("endian")));
        modbusClientPollingParams.setByteSwap(result.get("endian").equals("yes"));
    }

}
