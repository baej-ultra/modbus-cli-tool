package org.baej.modbusclitool.modbus.client.strategy;

import com.digitalpetri.modbus.pdu.ReadCoilsRequest;
import com.digitalpetri.modbus.pdu.ReadCoilsResponse;

public class DiscreteCoilsStrategy extends AbstractModbusRequestStrategy<ReadCoilsResponse> {

    public DiscreteCoilsStrategy() {
        super((client, unitId, address, quantity) -> {
            var req = new ReadCoilsRequest(address, quantity);
            return client.readCoils(unitId, req);
        }, ReadCoilsResponse::coils);
    }
}
