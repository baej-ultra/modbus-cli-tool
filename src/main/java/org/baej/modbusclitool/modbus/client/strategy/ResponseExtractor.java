package org.baej.modbusclitool.modbus.client.strategy;

import com.digitalpetri.modbus.pdu.ModbusResponsePdu;

@FunctionalInterface
public interface ResponseExtractor<R extends ModbusResponsePdu> {
    byte[] extract(R response);
}
