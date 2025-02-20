package org.baej.modbusclitool.modbus.server;

import com.digitalpetri.modbus.ExceptionCode;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.UnknownUnitIdException;
import com.digitalpetri.modbus.pdu.*;
import com.digitalpetri.modbus.server.ModbusRequestContext;
import com.digitalpetri.modbus.server.ModbusServices;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

@Service
public class ModbusServerService implements ModbusServices {

    private final Random random = new Random();

    @Override
    public ReadCoilsResponse readCoils(ModbusRequestContext context, int unitId, ReadCoilsRequest request) throws ModbusResponseException {
        var registers = getRandomBytes(request.getFunctionCode(), request.address(), (int) Math.ceil(request.quantity() / 8d));
        return new ReadCoilsResponse(registers);
    }

    @Override
    public ReadDiscreteInputsResponse readDiscreteInputs(ModbusRequestContext context, int unitId, ReadDiscreteInputsRequest request) throws ModbusResponseException {
        var registers = getRandomBytes(request.getFunctionCode(), request.address(), (int) Math.ceil(request.quantity() / 8d));
        return new ReadDiscreteInputsResponse(registers);
    }

    @Override
    public ReadHoldingRegistersResponse readHoldingRegisters(ModbusRequestContext context, int unitId, ReadHoldingRegistersRequest request) throws ModbusResponseException {
        byte[] registers = getRandomRegisters(request.getFunctionCode(), request.address(), request.quantity());
        return new ReadHoldingRegistersResponse(registers);
    }

    @Override
    public ReadInputRegistersResponse readInputRegisters(ModbusRequestContext context, int unitId, ReadInputRegistersRequest request) throws ModbusResponseException {
        byte[] registers = getRandomRegisters(request.getFunctionCode(), request.address(), request.quantity());
        return new ReadInputRegistersResponse(registers);
    }

    @Override
    public ReadWriteMultipleRegistersResponse readWriteMultipleRegisters(ModbusRequestContext context, int unitId, ReadWriteMultipleRegistersRequest request) throws ModbusResponseException, UnknownUnitIdException {
        return ModbusServices.super.readWriteMultipleRegisters(context, unitId, request);
    }

    /*
    Generate a bunch of random registers of various data types
     */

    private byte[] getRandomRegisters(int functionCode, int address, int quantity) throws ModbusResponseException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int lastAddress = buffer.capacity() / 2;

        if (address >= 0 && address <= lastAddress) {
            if (quantity >= 1 && quantity <= 125) {
                if (address + quantity > lastAddress + 1) {
                    throw new ModbusResponseException(functionCode, ExceptionCode.ILLEGAL_DATA_ADDRESS.getCode());
                }
            } else {
                throw new ModbusResponseException(functionCode, ExceptionCode.ILLEGAL_DATA_VALUE.getCode());
            }
        } else {
            throw new ModbusResponseException(functionCode, ExceptionCode.ILLEGAL_DATA_ADDRESS.getCode());
        }

        for (int i = 0; i < 5; i++) { // Short ints (addr 0-4)
            buffer.putShort((short) random.nextInt(Short.MAX_VALUE + 1));
        }
        for (int i = 0; i < 5; i++) { // Ints (ddr 5-14)
            buffer.putInt(random.nextInt());
        }
        for (int i = 0; i < 5; i++) { // Long ints (addr 15-34)
            buffer.putLong(random.nextLong());
        }
        for (int i = 0; i < 5; i++) { // Floats/Reals (addr 35-44)
            buffer.putFloat(random.nextFloat() * 10);
        }
        for (int i = 0; i < 5; i++) { // Double floats (addr 45-64)
            buffer.putDouble(random.nextDouble() * 100);
        }

        byte[] registers = buffer.array();
        int end = address + quantity * 2;
        return Arrays.copyOfRange(registers, address, end);
    }

    private byte[] getRandomBytes(int functionCode, int address, int quantity) throws ModbusResponseException {
        ByteBuffer buffer = ByteBuffer.allocate(3);

        int lastAddress = buffer.capacity();

        if (address >= 0 && address <= lastAddress) {
            if (quantity >= 1 && quantity <= 3) {
                if (address + quantity > lastAddress + 1) {
                    throw new ModbusResponseException(functionCode, ExceptionCode.ILLEGAL_DATA_ADDRESS.getCode());
                }
            } else {
                throw new ModbusResponseException(functionCode, ExceptionCode.ILLEGAL_DATA_VALUE.getCode());
            }
        } else {
            throw new ModbusResponseException(functionCode, ExceptionCode.ILLEGAL_DATA_ADDRESS.getCode());
        }

        byte[] randomByte = new byte[1];
        random.nextBytes(randomByte);
        buffer.put((byte) 0);
        buffer.put((byte) 0b11111111);
        buffer.put(randomByte);

        byte[] registers = buffer.array();
        int end = address + quantity;
        return Arrays.copyOfRange(registers, address, end);
    }
}
