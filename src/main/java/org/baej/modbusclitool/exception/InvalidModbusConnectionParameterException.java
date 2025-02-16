package org.baej.modbusclitool.exception;

public class InvalidModbusConnectionParameterException extends RuntimeException {
    public InvalidModbusConnectionParameterException(String message) {
        super(message);
    }
}
