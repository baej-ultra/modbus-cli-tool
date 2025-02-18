package org.baej.modbusclitool.modbus.client.strategy;

import com.digitalpetri.modbus.pdu.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.pdu.ReadHoldingRegistersResponse;

public class HoldingRegistersStrategy extends AbstractModbusRequestStrategy<ReadHoldingRegistersResponse> {

    public HoldingRegistersStrategy() {
        super((client, unitId, address, quantity) -> {
            var req = new ReadHoldingRegistersRequest(address, quantity);
            return client.readHoldingRegisters(unitId, req);
        }, ReadHoldingRegistersResponse::registers);
    }
}
