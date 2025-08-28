//package com.ss.system.util;
//
//import gnu.io.CommPortIdentifier;
//import gnu.io.SerialPort;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
///**
// * @ClassName DLSC_SerialPort
// * @Description 串口工具类 ：打开串口 ；接受数据 ；发送数据
// * @Version 1.0
// */
//public class DLSC_SerialPort {
//    private SerialPort serialPort;
//    private InputStream inputStream;
//    private OutputStream outputStream;
//    private BlockingQueue<String> receivedDataQueue = new LinkedBlockingQueue<>();
//    private volatile boolean isRunning = true;
//    private void connect(String portName) throws Exception {
//        // 获取串口通信端口实例
//        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
//        if (portId.isCurrentlyOwned()) {
//            System.err.println("Error: Port is currently in use");
//        } else {
//            // 打开端口，并获取输入、输出流
//            serialPort = (SerialPort) portId.open("SerialCommExample", 2000);
//            inputStream = serialPort.getInputStream();
//            outputStream = serialPort.getOutputStream();
//            // 设置串口参数
//            serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//            // 启动读取数据的线程
//            Thread readThread = new Thread(() -> {try {while (isRunning) {int available = inputStream.available();if (available > 0) {byte[] bytes = new byte[available];inputStream.read(bytes);String data = new String(bytes, "UTF-8");
//                            receivedDataQueue.put(data); // 将数据放入队列
//                }
//                        Thread.sleep(100); // 等待一段时间再检查是否有新数据
//                    }} catch (IOException | InterruptedException e) {e.printStackTrace();}});readThread.start();}}
//
//    private void sendData(String data) throws IOException {
//        if (outputStream != null) {
//            outputStream.write(data.getBytes("UTF-8"));
//            outputStream.flush(); // 确保数据被发送出去
//        }}
//
//
//    private String receiveData(long timeout) throws InterruptedException {
//        return receivedDataQueue.poll(timeout, TimeUnit.MILLISECONDS); // 尝试从队列中获取数据，超时则返回null
//    }
//    private void disconnect() throws Exception {
//        isRunning = false; // 设置标志位以停止读取数据的线程
//        if (serialPort != null) {
//            serialPort.removeEventListener();
//            serialPort.close();
//        }
//    }
//    public static void send (String msg) {
//        DLSC_SerialPort example = new DLSC_SerialPort();
//        try {
//            // 连接到串口
//            example.connect("COM3"); // 替换为你的串口名称
//            // 发送数据
//            example.sendData(msg + "\r\n");
//            // 断开连接
//            example.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    public static Double sendAndReceives(String msg) {
//        DLSC_SerialPort example = new DLSC_SerialPort();
//        try {
//            // 连接到串口
//            example.connect("COM1"); // 替换为你的串口名称
//            // 发送数据
//            example.sendData(msg + "\r\n");
//            // 等待并接收数据
//            String receivedData = example.receiveData(5000); // 等待最多5秒来获取数据
//            if (receivedData != null) {
//                Double backData = Double.valueOf(receivedData);
//                System.out.println("Received data: " + backData);
//                return backData;
//            } else {
//                System.out.println("No data received within the timeout period.");
//            }
//            // 断开连接
//            example.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
////    public static String sendAndReceive(String msg) {
////
////        SerialPortUtil serialPortUtil = SerialPortUtil.getSerialPortUtil();
////        ArrayList<String> port = serialPortUtil.findPort();
////        if (port != null && !port.isEmpty()) {
////            System.out.println("没有发现任何端口");
////        }
////        String portName = port.get(2);
////        SerialPort serialPort = serialPortUtil.openPort(
////                portName, 9600, 8, 1, 0);
////        if (serialPort == null) {
////            System.out.println("打开串口（" + portName + "）失败");
////        }
////        System.out.println("成功连接串口：" + portName);
//////        serialPortUtil.sendToPort(serialPort, "SOUR:VOLT 28\r\n".getBytes());
////        try {
////            Thread.sleep(1000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        serialPortUtil.sendToPort(serialPort, "SOUR:CURR:LIMIT:HIGH?\r\n".getBytes());
////        byte[] bytes = serialPortUtil.readFromPort(serialPort);
////        if (bytes != null) {
////            return new String(bytes);
////        }
////        serialPortUtil.closePort(serialPort);
////        return null;
////    }
//
//
////    public static String sendAndReceive(String msg) {
////
////        SerialPortUtil serialPortUtil = SerialPortUtil.getSerialPortUtil();
////        ArrayList<String> port = serialPortUtil.findPort();
////        if (port != null && !port.isEmpty()) {
////            System.out.println("没有发现任何端口");
////        }
////        String portName = port.get(0);
////        SerialPort serialPort = serialPortUtil.openPort(
////                portName, 9600, 8, 1, 0);
////        if (serialPort == null) {
////            System.out.println("打开串口（" + portName + "）失败");
////        }
////        System.out.println("成功连接串口：" + portName);
//////        serialPortUtil.sendToPort(serialPort, "SOUR:VOLT 28\r\n".getBytes());
////        try {
////            Thread.sleep(6000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        serialPortUtil.sendToPort(serialPort, "SOUR:CURR:LIMIT:HIGH?\r\n".getBytes());
////        byte[] bytes = serialPortUtil.readFromPort(serialPort);
////        if (bytes != null) {
////            return new String(bytes);
////        }
////        System.out.println(serialPort);
////        serialPortUtil.closePort(serialPort);
////        return null;
////    }
//
//
//    public static String sendAndReceive(String msg) {
//
//        SerialPortUtil serialPortUtil = SerialPortUtil.getSerialPortUtil();
//        ArrayList<String> port = serialPortUtil.findPort();
//        System.out.println(port);
//        if (port == null || port.isEmpty()) {
//            System.out.println("没有发现任何端口");
//            return null;
//        }
//        String portName = port.get(0);
//        SerialPort serialPort = serialPortUtil.openPort(portName, 9600, 8, 1, 0);
//        if (serialPort == null) {
//            System.out.println("打开串口（" + portName + "）失败");
//            return null;
//        }
//        System.out.println("成功连接串口：" + portName);
//        try {
//            // serialPortUtil.sendToPort(serialPort, "SOUR:VOLT 28\r\n".getBytes());
//            Thread.sleep(6000);
//            serialPortUtil.sendToPort(serialPort, "SOUR:CURR:LIMIT:HIGH?\r\n".getBytes());
//            byte[] bytes = serialPortUtil.readFromPort(serialPort);
//
//            if (bytes != null) {
//                return new String(bytes);
//            } else {
//                System.out.println("未读取到任何数据");
//                return null;
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            // 确保串口在所有情况下都关闭
//            serialPortUtil.closePort(serialPort);
//        }
//    }
//}