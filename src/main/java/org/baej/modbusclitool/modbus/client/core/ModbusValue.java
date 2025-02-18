package org.baej.modbusclitool.modbus.client.core;

public class ModbusValue {

    private int address;
    private Number value;

    public ModbusValue(int address, Number value) {
        this.address = address;
        this.value = value;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }
}
