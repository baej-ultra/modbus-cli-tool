package org.baej.modbusclitool.modbus.core;

public enum ModbusDataFormat {
    INT(2),
    LONG_INT(4),
    FLOAT(4),
    DOUBLE(8);

    private final int valueLength;

    ModbusDataFormat(int valueLength) {
        this.valueLength = valueLength;
    }

    public int getValueLength() {
        return valueLength;
    }
}
