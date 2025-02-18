package org.baej.modbusclitool.modbus.client.strategy;

import com.digitalpetri.modbus.client.ModbusClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.ModbusResponsePdu;
import org.baej.modbusclitool.modbus.client.ModbusClientPollingParams;
import org.baej.modbusclitool.modbus.client.core.ModbusData;

public class AbstractModbusRequestStrategy<R extends ModbusResponsePdu> implements ModbusRequestStrategy{

    private final ModbusRequestExecutor<R> executor;
    private final ResponseExtractor<R> extractor;

    public AbstractModbusRequestStrategy(ModbusRequestExecutor<R> executor, ResponseExtractor<R> extractor) {
        this.executor = executor;
        this.extractor = extractor;
    }

    @Override
    public ModbusData request(ModbusClient client, ModbusClientPollingParams params) {
        int address = params.getStartingAddress();
        int quantity = params.getQuantity();
        int unitId = params.getUnitId();
        byte[] registers;

        try {
            R response = executor.execute(client, unitId, address, quantity);
            registers = extractor.extract(response);
        } catch (ModbusExecutionException | ModbusTimeoutException | ModbusResponseException e) {
            throw new RuntimeException(e);
        }
        return new ModbusData(registers, params.getDataFormat(),
                params.getByteOrder(), params.isByteSwap());
    }


}
