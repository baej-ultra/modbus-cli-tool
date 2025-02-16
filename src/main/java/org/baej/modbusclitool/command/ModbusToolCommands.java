package org.baej.modbusclitool.command;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.baej.modbusclitool.exception.InvalidModbusConnectionParameterException;
import org.baej.modbusclitool.modbus.client.ModbusClientConnectionParams;
import org.baej.modbusclitool.modbus.client.ModbusConnectionManager;
import org.baej.modbusclitool.modbus.client.ModbusPollingService;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.VerticalAlign;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.io.Reader;

@ShellComponent
public class ModbusToolCommands {

    private final ModbusConnectionManager connectionManager;
    private final ModbusPollingService pollingService;
    private final ModbusClientConnectionParams connectionParams;
    private final ComponentFlow.Builder componentFlowBuilder;
    private final Terminal terminal;
    public volatile int i = 0;

    public ModbusToolCommands(ModbusConnectionManager connectionManager,
                              ModbusPollingService pollingService,
                              ModbusClientConnectionParams connectionParams, ComponentFlow.Builder componentFlowBuilder,
                              Terminal terminal) {
        this.connectionManager = connectionManager;
        this.pollingService = pollingService;
        this.connectionParams = connectionParams;
        this.componentFlowBuilder = componentFlowBuilder;
        this.terminal = terminal;
    }

    @ShellMethod(value = "Set modbus connection parameters")
    public void connectionParameters() {
        runConnectionParametersFlow();
    }

    @ShellMethod
    public void testPoll() throws IOException {
        // to be moved
        connectionManager.connect();
        pollingService.startPolling();

        Reader reader = terminal.reader();
        while (true) {
            if (reader.read() == 'q') {
                pollingService.stopPolling();
                terminal.writer().print("\033[H\033[2J");
                break;
            }
        }
    }

    @ShellMethod
    public void uiTest() {
        TerminalUI ui = new TerminalUI(terminal);

        Thread thread = new Thread(() -> {
            while (true) {
                i += 1;
                try {
                    Thread.sleep(1000);
                    ui.redraw();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        thread.start();

        BoxView boxView = new BoxView();
        boxView.setDrawFunction((screen, rect) -> {
            screen.writerBuilder().build()
                    .text(Integer.toString(i), rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
            return rect;
        });

        ui.configure(boxView);
        ui.setRoot(boxView, true);

        EventLoop eventLoop = ui.getEventLoop();
        eventLoop.keyEvents()
                .subscribe(event -> {
                    if (event.getPlainKey() == Key.q && event.hasCtrl()) {
                        eventLoop.dispatch(ShellMessageBuilder.ofInterrupt());
                    }
                });
        ui.run();
    }

    private void runConnectionParametersFlow() {
        InetAddressValidator inetValidator = InetAddressValidator.getInstance();

        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withStringInput("host")
                .name("IP address")
                .defaultValue(connectionParams.getHost())
                .and()
                .withStringInput("port")
                .name("Port")
                .defaultValue(Integer.toString(connectionParams.getPort()))
                .and()
                .withStringInput("timeout")
                .name("Connection timeout")
                .defaultValue(Integer.toString(connectionParams.getTimeout()))
                .and()
                .build();

        var result = flow
                .run()
                .getContext();

        // get values from the flow
        String host = result.get("host");
        int port = Integer.parseInt(result.get("port"));
        int timeout = Integer.parseInt(result.get("timeout"));

        if (!inetValidator.isValid(host)) {
            throw new InvalidModbusConnectionParameterException("Invalid address");
        }

        if (port < 0 || port > 65535) {
            throw new InvalidModbusConnectionParameterException("Invalid port");
        }

        if (timeout < 0) {
            throw new InvalidModbusConnectionParameterException("Invalid timeout");
        }

        connectionParams.setHost(host);
        connectionParams.setPort(port);
        connectionParams.setTimeout(timeout);
    }

}
