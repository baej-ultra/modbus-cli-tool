package org.baej.modbusclitool.modbus.client;

import com.digitalpetri.modbus.client.ModbusClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.ReadHoldingRegistersRequest;
import org.baej.modbusclitool.modbus.core.ModbusData;

public class HoldingRegistersStrategy implements ModbusRequestStrategy {

    @Override
    public ModbusData request(ModbusClient client, ModbusClientPollingParams params) {
        int address = params.getStartingAddress();
        int quantity = params.getQuantity();
        int unitId = params.getUnitId();
        byte[] registers;

        try {
            var req = new ReadHoldingRegistersRequest(address, quantity);
            var response = client.readHoldingRegisters(unitId, req);
            registers = response.registers();
        } catch (ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e) {
            throw new RuntimeException(e);
        }

        return new ModbusData(registers, params.getDataFormat(),
                params.getByteOrder(), params.isByteSwap());
    }
}
