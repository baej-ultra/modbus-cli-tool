package org.baej.modbusclitool.modbus.client.strategy;

import com.digitalpetri.modbus.pdu.ReadInputRegistersRequest;
import com.digitalpetri.modbus.pdu.ReadInputRegistersResponse;

public class InputRegistersStrategy extends AbstractModbusRequestStrategy<ReadInputRegistersResponse> {

    public InputRegistersStrategy() {
        super((client, unitId, address, quantity) -> {
            var req = new ReadInputRegistersRequest(address, quantity);
            return client.readInputRegisters(unitId, req);
        }, ReadInputRegistersResponse::registers);
    }
}
