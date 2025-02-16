package org.baej.modbusclitool.modbus.core;

public interface ModbusDataObserver {

    void onNewData(ModbusData data);

}
