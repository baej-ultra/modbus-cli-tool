package org.baej.modbusclitool.modbus.client;

import org.springframework.stereotype.Component;

@Component
public class ModbusClientConnectionParams {

    private String host = "127.0.0.1";
    private int port = 502;
    private int timeout = 5000;

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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
