package org.baej.modbusclitool.view;

import org.baej.modbusclitool.modbus.client.ModbusClientPollingParams;
import org.baej.modbusclitool.modbus.client.core.ModbusData;
import org.baej.modbusclitool.modbus.client.core.ModbusDataObserver;
import org.baej.modbusclitool.modbus.client.core.ModbusValue;
import org.jline.terminal.Terminal;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class ModbusTerminalDisplay implements ModbusDataObserver {

    private final ModbusClientPollingParams modbusClientPollingParams;
    private final Terminal terminal;

    public ModbusTerminalDisplay(ModbusClientPollingParams modbusClientPollingParams, Terminal terminal) {
        this.modbusClientPollingParams = modbusClientPollingParams;
        this.terminal = terminal;
    }

    /*
    This could easily be a method called by polling service,
    but I wanted to implement an observer pattern
     */
    @Override
    public void onNewData(ModbusData data) {
        // Table headers
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("address", "Offset");
        headers.put("value", "Value");

        // Table data
        BeanListTableModel<ModbusValue> tableModel = new BeanListTableModel<ModbusValue>(
                data.getValues(),
                headers
        );

        // Build table
        Table table = new TableBuilder(tableModel)
                .addFullBorder(BorderStyle.fancy_double)
                .build();

        // Print to console
        terminal.writer().print(table.render(100));
        terminal.writer().printf("Starting address:%s|Format:%s,%s|Byte swap:%s\n",
                modbusClientPollingParams.getStartingAddress(),
                data.getDataFormat(),
                data.getByteOrder(),
                data.isByteSwap()
        );
        terminal.flush();
    }
}
