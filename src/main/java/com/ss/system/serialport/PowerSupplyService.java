package com.ss.system.serialport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PowerSupplyService {
    private final SerialPortService serialPortService;
    
    // 设置远程控制模式
    public void setRemoteMode() throws Exception {
        serialPortService.sendCommand("SYST:REM");
        log.info("Set power supply to remote control mode");
    }
    
    // 设置电压
    public void setVoltage(double voltage) throws Exception {
        String command = String.format("VOLT %.2f", voltage);
        serialPortService.sendCommand(command);
        log.info("Voltage set to {}V", voltage);
    }
    
    // 设置电流
    public void setCurrent(double current) throws Exception {
        String command = String.format("CURR %.2f", current);
        serialPortService.sendCommand(command);
        log.info("Current set to {}A", current);
    }
    
    // 开启输出
    public void enableOutput() throws Exception {
        serialPortService.sendCommand("OUTP ON");
        log.info("Output enabled");
    }
    
    // 关闭输出
    public void disableOutput() throws Exception {
        serialPortService.sendCommand("OUTP OFF");
        log.info("Output disabled");
    }
    
    // 查询当前电压
    public double getVoltage() throws Exception {
        serialPortService.sendCommand("MEAS:VOLT?");
        String response = serialPortService.readResponse();
        return Double.parseDouble(response);
    }
    
    // 查询当前电流
    public double getCurrent() throws Exception {
        serialPortService.sendCommand("MEAS:CURR?");
        String response = serialPortService.readResponse();
        return Double.parseDouble(response);
    }
    
    // 设置过压保护
    public void setOverVoltageProtection(double voltage) throws Exception {
        String command = String.format("VOLT:PROT %.2f", voltage);
        serialPortService.sendCommand(command);
        log.info("Over voltage protection set to {}V", voltage);
    }
    
    // 清除保护状态
    public void clearProtection() throws Exception {
        serialPortService.sendCommand("VOLT:PROT:CLE");
        log.info("Protection state cleared");
    }
}
