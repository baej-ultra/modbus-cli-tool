package org.baej.modbusclitool.modbus.client.strategy;

import com.digitalpetri.modbus.client.ModbusClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.ModbusResponsePdu;

@FunctionalInterface
public interface ModbusRequestExecutor<R extends ModbusResponsePdu> {

    R execute(ModbusClient client, int unitId, int address, int quantity)
            throws ModbusExecutionException, ModbusResponseException, ModbusTimeoutException;

}
