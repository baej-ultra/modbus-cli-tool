package org.baej.modbusclitool.modbus.client.strategy;

import com.digitalpetri.modbus.client.ModbusClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.ReadCoilsRequest;
import org.baej.modbusclitool.modbus.client.ModbusClientPollingParams;
import org.baej.modbusclitool.modbus.core.ModbusData;

public class DiscreteCoilsStrategy implements ModbusRequestStrategy {

    @Override
    public ModbusData request(ModbusClient client, ModbusClientPollingParams params) {
        int address = params.getStartingAddress();
        int quantity = params.getQuantity();
        int unitId = params.getUnitId();
        byte[] registers;

        try {
            var req = new ReadCoilsRequest(address, quantity);
            var response = client.readCoils(unitId, req);
            registers = response.coils();
        } catch (ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e) {
            throw new RuntimeException(e);
        }

        return new ModbusData(registers, params.getDataFormat(),
                params.getByteOrder(), params.isByteSwap());
    }
}
