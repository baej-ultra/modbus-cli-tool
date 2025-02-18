package org.baej.modbusclitool.modbus.client.core;

public interface ModbusDataObserver {

    void onNewData(ModbusData data);

}
