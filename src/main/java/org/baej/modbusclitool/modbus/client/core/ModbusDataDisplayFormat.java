package org.baej.modbusclitool.modbus.client.core;

public enum ModbusDataDisplayFormat {
    BINARY(1),
    BYTE(1),
    SHORT_INT(2),
    INT(4),
    LONG_INT(8),
    FLOAT(4),
    DOUBLE(8);

    private final int valueLength;

    ModbusDataDisplayFormat(int valueLength) {
        this.valueLength = valueLength;
    }

    public int getValueLength() {
        return valueLength;
    }
}
