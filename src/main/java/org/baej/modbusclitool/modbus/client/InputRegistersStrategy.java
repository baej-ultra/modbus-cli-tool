package org.baej.modbusclitool.modbus.client;

import com.digitalpetri.modbus.client.ModbusClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.ReadInputRegistersRequest;
import org.baej.modbusclitool.modbus.core.ModbusData;

public class InputRegistersStrategy implements ModbusRequestStrategy {

    @Override
    public ModbusData request(ModbusClient client, ModbusClientPollingParams params) {
        int address = params.getStartingAddress();
        int quantity = params.getQuantity();
        int unitId = params.getUnitId();
        byte[] registers;

        try {
            var req = new ReadInputRegistersRequest(address, quantity);
            var response = client.readInputRegisters(unitId, req);
            registers = response.registers();
        } catch (ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e) {
            throw new RuntimeException(e);
        }

        return new ModbusData(registers, params.getDataFormat(),
                params.getByteOrder(), params.isByteSwap());
    }
}
