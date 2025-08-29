package com.ss.system.util;


import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.ss.system.common.dto.RespondDto;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.*;


/**
 *  测试大屏 : 2024-7-19流程图测试————2025。8.29更新
 *
 *  @Description  : JSON格式化，压缩 在线工具  https://www.indev.cn/
 *
 *  注意点：按钮的URL 要与之对应起来，路径不要写错，注意跨域
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/ssmonitor/server/liuchengtu")
public class PostCtronller {

    /**
     * 传入对应的测试项——加载对应的流程图数据
     */
    @RequestMapping(value = "upload", method = {RequestMethod.GET, RequestMethod.POST})
//    public void uploadSSFlowData(@RequestParam String fileName) throws IOException {
    public void uploadSSFlowData() throws IOException {
        System.out.println("1111111111111111");
        // 定义目标 URL
        String url = "http://192.168.65.126:22324/ssmonitor/server/sse/send/one";
        // 从 resources 目录读取 JSON 文件并转换为字符串
//        String postData = readJsonFileFromResources(fileName);
        String example;
        example = "example.json";
        String postData = readJsonFileFromResources(example);

        // 发送 JSON 数据
        sendFlowchartData(url, postData);
    }

    /**
     * 从 resources 目录读取 JSON 文件并返回其内容
     *
     * @param fileName 文件名（例如：example.json）
     * @return JSON 文件内容
     * @throws IOException 如果文件读取失败
     */
    private String readJsonFileFromResources(String fileName) throws IOException {
        // 获取 resources 目录下的文件路径
        File file = ResourceUtils.getFile("classpath:" + fileName);
        // 读取文件内容为字符串
        return new String(Files.readAllBytes(file.toPath()));
    }

    /**
     * 传入对应的测试项——加载对应的流程图数据（模拟自动化测试流程）
     */
//    @RequestMapping(value = "start", method = {RequestMethod.GET, RequestMethod.POST})
//    public void getSSFlowData() throws InterruptedException, IOException {
//        System.out.println("222222222222222222");
//
//        // 定义目标 URL
////        String url = "http://172.18.100.115:22324//ssmonitor/server/sse/send/one";
//        String url = "http://192.168.65.126:22324/ssmonitor/server/sse/send/one";
//        String example;
//        example = "example.json";
//        // 通过 example.json 读取初始流程图数据
//        String initialFlowchartJson = readJsonFileFromResources(example);
//
//        // 动态请求列表
//        List<Object[]> updateRequests = new ArrayList<>();
//
//        // 当前使用的 JSON 字符串（初始化为初始 JSON）
//        String currentFlowchartJson = initialFlowchartJson;
//
//        // 模拟多次请求
//        for (int i = 0; i < 5; i++) { // 假设发送 5 次请求
//            // 清空之前的请求内容
//            updateRequests.clear();
//            // 根据请求次数动态添加内容
//            if (i == 0) {
//                // 第一次请求  开始正在运行
//                updateRequests.add(new Object[]{"start", 2});
//            } else if (i == 1) {
//                // 第二次请求  开始运行结束 "关闭电源并设置输出电流10A"  正在运行   关闭负载1-12通道使能
//                updateRequests.add(new Object[]{"start", 3});
//                updateRequests.add(new Object[]{"关闭电源并设置输出电流10A", 2});
//            } else if (i == 2) {
//                // 第三次请求
//                updateRequests.add(new Object[]{"关闭电源并设置输出电流10A", 3});
//                updateRequests.add(new Object[]{"关闭负载1-12通道使能", 2});
//            } else if (i == 3) {
//                // 第四次请求
//                updateRequests.add(new Object[]{"关闭负载1-12通道使能", 3});
//                updateRequests.add(new Object[]{"设置负载1-12通道0A电流", 2});
//            } else if (i == 4) {
//                // 第五次请求
//                updateRequests.add(new Object[]{"设置负载1-12通道0A电流", 3});
//            }
//            // 遍历对象数组
//            for (Object[] request : updateRequests) {
//                // 获取当前请求的 nodeName 和 newState
//                String nodeName = (String) request[0];
//                Integer newState = (Integer) request[1];
//                // 调用工具类方法更新流程图节点状态
//                String updatedFlowchartJson = FlowchartUtil.updateNodeState(currentFlowchartJson, nodeName, newState);
//                System.out.println("Updated Flowchart JSON: " + updatedFlowchartJson);
//
//                // 将修改后的 JSON 字符串发送到指定 URL
//                if (updatedFlowchartJson != null) {
//                    sendFlowchartData(url, updatedFlowchartJson);
//                }
//                // 更新当前 JSON 字符串为最新返回的结果
//                currentFlowchartJson = updatedFlowchartJson;
//                // 等待 3 秒后再处理下一个对象
//                Thread.sleep(3000);
//            }
//        }
//    }


    @RequestMapping(value = "start", method = {RequestMethod.GET, RequestMethod.POST})
    public void getSSFlowData() throws InterruptedException, IOException {
        System.out.println("222222222222222222");

        // 定义目标 URL
        String url = "http://192.168.65.126:22324/ssmonitor/server/sse/send/one";
        String example = "example.json";

        // 通过 example.json 读取初始流程图数据
        String initialFlowchartJson = readJsonFileFromResources(example);

        // 动态请求列表
        List<Object[]> updateRequests = new ArrayList<>();

        // 当前使用的 JSON 字符串（初始化为初始 JSON）
        String currentFlowchartJson = initialFlowchartJson;

        // 模拟多次请求
        for (int i = 0; i < 5; i++) { // 假设发送 5 次请求
            // 清空之前的请求内容
            updateRequests.clear();

            // 根据请求次数动态添加内容
            if (i == 0) {
                // 第一次请求  开始正在运行
                updateRequests.add(new Object[]{"start", 2});
            } else if (i == 1) {
                // 第二次请求  开始运行结束 "关闭电源并设置输出电流10A"  正在运行   关闭负载1-12通道使能
                updateRequests.add(new Object[]{"start", 3});
                updateRequests.add(new Object[]{"关闭电源并设置输出电流10A", 2});
            } else if (i == 2) {
                // 第三次请求
                updateRequests.add(new Object[]{"关闭电源并设置输出电流10A", 3});
                updateRequests.add(new Object[]{"关闭负载1-12通道使能", 2});
            } else if (i == 3) {
                // 第四次请求
                updateRequests.add(new Object[]{"关闭负载1-12通道使能", 3});
                updateRequests.add(new Object[]{"设置负载1-12通道0A电流", 2});
            } else if (i == 4) {
                // 第五次请求
                updateRequests.add(new Object[]{"设置负载1-12通道0A电流", 3});
            }

            // 遍历对象数组
            for (Object[] request : updateRequests) {
                // 获取当前请求的 nodeName 和 newState
                String nodeName = (String) request[0];
                Integer newState = (Integer) request[1];

                // 确定唯一节点 ID
                String uniqueNodeId = FlowchartUtil.determineUniqueNodeId(currentFlowchartJson, nodeName);
                if (uniqueNodeId == null) {
                    System.err.println("无法确定节点 [" + nodeName + "] 的唯一 ID！");
                    continue;
                }

                // 调用工具类方法更新流程图节点状态
                String updatedFlowchartJson = FlowchartUtil.updateNodeStateById(currentFlowchartJson, uniqueNodeId, newState);
                System.out.println("Updated Flowchart JSON: " + updatedFlowchartJson);

                // 将修改后的 JSON 字符串发送到指定 URL
                if (updatedFlowchartJson != null) {
                    sendFlowchartData(url, updatedFlowchartJson);
                }

                // 更新当前 JSON 字符串为最新返回的结果
                currentFlowchartJson = updatedFlowchartJson;

                // 等待 3 秒后再处理下一个对象
                Thread.sleep(3000);
            }
        }
    }




    private void sendFlowchartData(String url, String updatedFlowchartJson) throws IOException {

        // 创建URL对象
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // 设置请求方法和请求头
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        // 发送POST请求
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.write(updatedFlowchartJson.getBytes("UTF-8"));
        wr.flush();
        wr.close();
        // 获取响应
        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // 打印响应结果
        System.out.println("Response: " + response.toString());
        // 遍历完一遍后停止
        System.out.println("All postData sent. Exiting...");
    }
}
