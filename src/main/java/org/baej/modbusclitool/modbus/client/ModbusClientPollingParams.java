package org.baej.modbusclitool.modbus.client;

import org.baej.modbusclitool.modbus.core.ModbusDataByteOrder;
import org.baej.modbusclitool.modbus.core.ModbusDataFormat;
import org.springframework.stereotype.Component;

@Component
public class ModbusClientPollingParams {

    private int unitId = 1;
    private int startingAddress = 0;
    private int quantity = 10;
    private ModbusDataFormat dataFormat = ModbusDataFormat.FLOAT;
    private ModbusDataByteOrder byteOrder = ModbusDataByteOrder.BIG_ENDIAN;
    private boolean byteSwap = false;

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public ModbusDataFormat getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(ModbusDataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    public ModbusDataByteOrder getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(ModbusDataByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public boolean isByteSwap() {
        return byteSwap;
    }

    public void setByteSwap(boolean byteSwap) {
        this.byteSwap = byteSwap;
    }

    public int getStartingAddress() {
        return startingAddress;
    }

    public void setStartingAddress(int startingAddress) {
        this.startingAddress = startingAddress;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
