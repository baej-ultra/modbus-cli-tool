package org.baej.modbusclitool.modbus.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static org.baej.modbusclitool.modbus.core.ModbusDataByteOrder.BIG_ENDIAN_SWAP;
import static org.baej.modbusclitool.modbus.core.ModbusDataByteOrder.SMALL_ENDIAN_SWAP;

public class ModbusData {

    private final Map<Integer, Number> values = new TreeMap<>();

    public ModbusData(byte[] dataBytes, ModbusDataFormat dataFormat, ModbusDataByteOrder byteOrder) {
        processData(dataBytes, dataFormat, byteOrder);
    }

    private void processData(byte[] dataBytes, ModbusDataFormat dataFormat, ModbusDataByteOrder byteOrder) {
        values.clear();

        // TODO Consider truncating the byte array to fit selected data type ad appending value map
        //
        if (dataBytes.length % dataFormat.getValueLength() != 0) {
            throw new IllegalArgumentException("Input byte array length doesn't match selected data format");
        }

        // Byte swapping for those rare occurrences when manufacturers
        // get high and decide to implement modbus this way
        if (byteOrder == BIG_ENDIAN_SWAP || byteOrder == SMALL_ENDIAN_SWAP) {
            byte temp;
            for (int i = 0; i < dataBytes.length - 1; i++) {
                temp = dataBytes[i];
                dataBytes[i] = dataBytes[i + 1];
                dataBytes[i + 1] = temp;
            }
        }

        // Byte order
        ByteOrder bo = switch (byteOrder) {
            case BIG_ENDIAN, BIG_ENDIAN_SWAP -> ByteOrder.BIG_ENDIAN;
            case SMALL_ENDIAN, SMALL_ENDIAN_SWAP -> ByteOrder.LITTLE_ENDIAN;
        };

        int valueLength = dataFormat.getValueLength();
        int valueCount = dataBytes.length / valueLength;
        for (int i = 0; i < valueCount; i++) {
            int index = i * dataFormat.getValueLength();
            var buffer = ByteBuffer.wrap(dataBytes, index, valueLength).order(bo);
            switch (dataFormat){
                case INT:
                    values.put(index, buffer.getShort());
                    break;
                case LONG_INT:
                    values.put(index, buffer.getInt());
                    break;
                case FLOAT:
                    values.put(index, buffer.getFloat());
                    break;
                case DOUBLE:
                    values.put(index, buffer.getDouble());
                    break;
            }
        }
    }

    public Map<Integer, Number> getValues() {
        return Collections.unmodifiableMap(values);
    }

}