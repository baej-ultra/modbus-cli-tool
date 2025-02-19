package org.baej.modbusclitool.modbus.server;

import com.digitalpetri.modbus.ExceptionCode;
import com.digitalpetri.modbus.FunctionCode;
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
    @Override
    public ReadCoilsResponse readCoils(ModbusRequestContext context, int unitId, ReadCoilsRequest request) throws ModbusResponseException, UnknownUnitIdException {
        return ModbusServices.super.readCoils(context, unitId, request);
    }

    @Override
    public ReadDiscreteInputsResponse readDiscreteInputs(ModbusRequestContext context, int unitId, ReadDiscreteInputsRequest request) throws ModbusResponseException, UnknownUnitIdException {
        return ModbusServices.super.readDiscreteInputs(context, unitId, request);
    }

    @Override
    public ReadHoldingRegistersResponse readHoldingRegisters(ModbusRequestContext context, int unitId, ReadHoldingRegistersRequest request) throws ModbusResponseException, UnknownUnitIdException {
        Random random = new Random();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // If request is bigger than buffer return Illegal Data Address error to client
        if (request.address() >= buffer.capacity() / 2 || request.address() + request.quantity() > buffer.capacity() / 2) {
            throw new ModbusResponseException(FunctionCode.READ_HOLDING_REGISTERS, ExceptionCode.ILLEGAL_DATA_ADDRESS);
        }

        for (int i = 0; i < 5; i++) {
            buffer.putShort((short) random.nextInt(Short.MAX_VALUE + 1));
        }
        for (int i = 0; i < 5; i++) {
            buffer.putInt(random.nextInt());
        }
        for (int i = 0; i < 5; i++) {
            buffer.putLong(random.nextLong());
        }
        for (int i = 0; i < 5; i++) {
            buffer.putFloat(random.nextFloat()*10);
        }
        for (int i = 0; i < 5; i++) {
            buffer.putDouble(random.nextDouble()*100);
        }

        byte[] registers = buffer.array();
        int start = request.address();
        int end = request.address() + (request.quantity() * 2);

        return new ReadHoldingRegistersResponse(Arrays.copyOfRange(registers, start, end));
    }

    @Override
    public ReadInputRegistersResponse readInputRegisters(ModbusRequestContext context, int unitId, ReadInputRegistersRequest request) throws ModbusResponseException, UnknownUnitIdException {
        return ModbusServices.super.readInputRegisters(context, unitId, request);
    }

    @Override
    public WriteSingleCoilResponse writeSingleCoil(ModbusRequestContext context, int unitId, WriteSingleCoilRequest request) throws ModbusResponseException, UnknownUnitIdException {
        return ModbusServices.super.writeSingleCoil(context, unitId, request);
    }

    @Override
    public WriteSingleRegisterResponse writeSingleRegister(ModbusRequestContext context, int unitId, WriteSingleRegisterRequest request) throws ModbusResponseException, UnknownUnitIdException {
        return ModbusServices.super.writeSingleRegister(context, unitId, request);
    }

    @Override
    public WriteMultipleCoilsResponse writeMultipleCoils(ModbusRequestContext context, int unitId, WriteMultipleCoilsRequest request) throws ModbusResponseException, UnknownUnitIdException {
        return ModbusServices.super.writeMultipleCoils(context, unitId, request);
    }

    @Override
    public WriteMultipleRegistersResponse writeMultipleRegisters(ModbusRequestContext context, int unitId, WriteMultipleRegistersRequest request) throws ModbusResponseException, UnknownUnitIdException {
        return ModbusServices.super.writeMultipleRegisters(context, unitId, request);
    }

    @Override
    public MaskWriteRegisterResponse maskWriteRegister(ModbusRequestContext context, int unitId, MaskWriteRegisterRequest request) throws ModbusResponseException, UnknownUnitIdException {
        return ModbusServices.super.maskWriteRegister(context, unitId, request);
    }

    @Override
    public ReadWriteMultipleRegistersResponse readWriteMultipleRegisters(ModbusRequestContext context, int unitId, ReadWriteMultipleRegistersRequest request) throws ModbusResponseException, UnknownUnitIdException {
        return ModbusServices.super.readWriteMultipleRegisters(context, unitId, request);
    }
}
