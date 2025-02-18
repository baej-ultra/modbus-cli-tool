package org.baej.modbusclitool.modbus.client.strategy;

import com.digitalpetri.modbus.pdu.ReadDiscreteInputsRequest;
import com.digitalpetri.modbus.pdu.ReadDiscreteInputsResponse;

public class DiscreteInputsStrategy extends AbstractModbusRequestStrategy<ReadDiscreteInputsResponse> {

    public DiscreteInputsStrategy() {
        super((client, unitId, address, quantity) -> {
            var req = new ReadDiscreteInputsRequest(address, quantity);
            return client.readDiscreteInputs(unitId, req);
        }, ReadDiscreteInputsResponse::inputs);
    }
}
