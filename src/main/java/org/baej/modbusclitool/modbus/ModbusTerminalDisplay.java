package org.baej.modbusclitool.modbus;

import org.baej.modbusclitool.modbus.client.ModbusClientPollingParams;
import org.baej.modbusclitool.modbus.core.ModbusData;
import org.baej.modbusclitool.modbus.core.ModbusDataObserver;
import org.baej.modbusclitool.modbus.core.ModbusValue;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class ModbusTerminalDisplay implements ModbusDataObserver {


    private final ModbusClientPollingParams modbusClientPollingParams;

    public ModbusTerminalDisplay(ModbusClientPollingParams modbusClientPollingParams) {
        this.modbusClientPollingParams = modbusClientPollingParams;
    }

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
        System.out.println(table.render(100));
        System.out.printf("Starting address:%s Format:%s,%s Byte swap:%s\n",
                modbusClientPollingParams.getStartingAddress(),
                data.getDataFormat(),
                data.getByteOrder(),
                data.isByteSwap()
        );
    }
}
