package com.ss.system.serialport;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SerialPortService {
    private final SerialPortConfig config;
    private SerialPort serialPort;

    public void initialize() throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(config.getPortName());
        if (portIdentifier.isCurrentlyOwned()) {
            throw new RuntimeException("Port is currently in use");
        }

        serialPort = (SerialPort) portIdentifier.open("IT6900Control", 2000);
        serialPort.setSerialPortParams(
                config.getBaudRate(),
                config.getDataBits(),
                config.getStopBits(),
                SerialPort.PARITY_NONE
        );

        log.info("Serial port {} initialized successfully", config.getPortName());
    }

    public void sendCommand(String command) throws Exception {
        if (serialPort == null) {
            throw new RuntimeException("Serial port not initialized");
        }

        OutputStream out = serialPort.getOutputStream();
        out.write((command + "\n").getBytes());
        out.flush();
        log.debug("Command sent: {}", command);
    }

    public String readResponse() throws Exception {
        if (serialPort == null) {
            throw new RuntimeException("Serial port not initialized");
        }

        InputStream in = serialPort.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.readLine();
    }

    @PreDestroy
    public void close() {
        if (serialPort != null) {
            serialPort.close();
            log.info("Serial port {} closed", config.getPortName());
        }
    }
}
