package com.ss.system.serialport;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "serial")
@Data
public class SerialPortConfig {
    private String portName;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private String parity;
}

