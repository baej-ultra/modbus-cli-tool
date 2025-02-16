package org.baej.modbusclitool.modbus;

import org.baej.modbusclitool.modbus.core.ModbusData;
import org.baej.modbusclitool.modbus.core.ModbusDataObserver;

public class ModbusTerminalDisplay implements ModbusDataObserver {
    @Override
    public void onNewData(ModbusData data) {
        //TODO
        System.out.println(data.getValues());
    }
}
