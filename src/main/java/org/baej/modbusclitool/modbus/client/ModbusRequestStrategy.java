package org.baej.modbusclitool.modbus.client;

import com.digitalpetri.modbus.client.ModbusClient;
import org.baej.modbusclitool.modbus.core.ModbusData;

public interface ModbusRequestStrategy {

    ModbusData request(ModbusClient client, ModbusClientPollingParams params);

}
