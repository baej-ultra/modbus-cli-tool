package org.baej.modbusclitool.modbus.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class ModbusData {

    private final List<ModbusValue> values;
    private final ModbusDataDisplayFormat dataFormat;
    private final ModbusDataByteOrder byteOrder;
    private final boolean byteSwap;

    public ModbusData(byte[] dataBytes, ModbusDataDisplayFormat dataFormat, ModbusDataByteOrder byteOrder, boolean byteSwap) {
        values = new ArrayList<>(dataBytes.length / 4);
        this.byteSwap = byteSwap;
        this.dataFormat = dataFormat;
        this.byteOrder = byteOrder;
        processData(dataBytes);
    }

    public List<ModbusValue> getValues() {
        return Collections.unmodifiableList(values);
    }

    public ModbusDataDisplayFormat getDataFormat() {
        return dataFormat;
    }

    public ModbusDataByteOrder getByteOrder() {
        return byteOrder;
    }

    public boolean isByteSwap() {
        return byteSwap;
    }

    private void processData(byte[] dataBytes) {
        values.clear();

        // TODO Consider truncating the byte array to fit selected data type and appending value map with short ints
        if (dataBytes.length % dataFormat.getValueLength() != 0) {
            throw new IllegalArgumentException("Register range doesn't match selected data format");
        }

        // Byte swapping for those rare occurrences when manufacturers
        // get high and decide to implement modbus this way
        if (byteSwap) {
            byte temp;
            for (int i = 0; i < dataBytes.length - 1; i++) {
                temp = dataBytes[i];
                dataBytes[i] = dataBytes[i + 1];
                dataBytes[i + 1] = temp;
            }
        }

        // Byte order
        ByteOrder bo = switch (byteOrder) {
            case BIG_ENDIAN -> ByteOrder.BIG_ENDIAN;
            case SMALL_ENDIAN -> ByteOrder.LITTLE_ENDIAN;
        };

        // Parse data
        int valueLength = dataFormat.getValueLength();
        int valueCount = dataBytes.length / valueLength;
        for (int i = 0; i < valueCount; i++) {
            int index = i * dataFormat.getValueLength(); // one register is two bytes
            var buffer = ByteBuffer.wrap(dataBytes, index, valueLength).order(bo);
            switch (dataFormat) {
                case SHORT_INT -> values.add(new ModbusValue(index/2, buffer.getShort()));
                case INT -> values.add(new ModbusValue(index/2, buffer.getInt()));
                case LONG_INT -> values.add(new ModbusValue(index/2, buffer.getLong()));
                case FLOAT -> values.add(new ModbusValue(index/2, buffer.getFloat()));
                case DOUBLE -> values.add(new ModbusValue(index/2, buffer.getDouble()));
            }
        }
    }

}