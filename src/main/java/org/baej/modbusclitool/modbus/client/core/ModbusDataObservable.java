package org.baej.modbusclitool.modbus.client.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class ModbusDataObservable {

    Set<ModbusDataObserver> observers = new HashSet<>();

    protected void notifyObservers(ModbusData modbusData) {
        for (ModbusDataObserver observer : observers) {
            observer.onNewData(modbusData);
        }
    }

    public void subscribe(ModbusDataObserver... observers) {
        this.observers.addAll(Arrays.asList(observers));
    }

    public void unsubscribe(ModbusDataObserver... observers) {
        Arrays.asList(observers).forEach(this.observers::remove);
    }
}
