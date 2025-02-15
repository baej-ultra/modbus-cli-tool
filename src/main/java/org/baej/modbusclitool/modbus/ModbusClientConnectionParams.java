package org.baej.modbusclitool.modbus;

import org.springframework.stereotype.Component;

@Component
public class ModbusClientConnectionParams {

    private String host = "192.168.94.10";
    private int port = 502;
    private int pollingInverval = 1000;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPollingInverval() {
        return pollingInverval;
    }

    public void setPollingInverval(int pollingInverval) {
        this.pollingInverval = pollingInverval;
    }
}
