package com.ss.system.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FlowchartUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 更新流程图中指定节点的状态值。
     *
     * @param flowchartJson 流程图的 JSON 字符串
     * @param nodeName      要匹配的节点名称（支持换行符） 【其中 开始 是 start  结束是 end 】
     * @param newState      新的状态值  【 1是初始状态（浅蓝色） 2 是运行状态（深蓝色） 3 是完成状态（绿色） 4 是错误状态（红色）】
     * @return 修改后的 JSON 字符串，如果未找到匹配节点则返回 null
     */
    public static String updateNodeState(String flowchartJson, String nodeName, Integer newState) {
        try {
            JsonNode rootNode = objectMapper.readTree(flowchartJson);
            System.out.println("JSON 解析成功！");

            JsonNode nodesArray = rootNode.at("/reqData/data/nodes");
            if (nodesArray == null || !nodesArray.isArray()) {
                System.err.println("未找到有效的 nodes 数组！");
                return null;
            }

            boolean isUpdated = false;
            String targetNodeName = nodeName.replace("\n", ""); // 统一处理换行符

            for (JsonNode node : nodesArray) {
                JsonNode typeNode = node.path("type");
                JsonNode propertiesNode = node.path("properties");
                JsonNode stateNode = propertiesNode.path("state");

                // 1. 如果是 start 或 end 节点，且 nodeName 匹配类型
                if ("start".equals(typeNode.asText()) && "start".equals(targetNodeName)) {
                    if (stateNode.isInt()) {
                        int oldState = stateNode.asInt();
                        ((ObjectNode) propertiesNode).put("state", newState);
                        System.out.printf("节点 [type=%s] 的状态已从 %d 更新为 %d%n", typeNode.asText(), oldState, newState);
                        isUpdated = true;
                    }
                }
                else if ("end".equals(typeNode.asText()) && "end".equals(targetNodeName)) {
                    if (stateNode.isInt()) {
                        int oldState = stateNode.asInt();
                        ((ObjectNode) propertiesNode).put("state", newState);
                        System.out.printf("节点 [type=%s] 的状态已从 %d 更新为 %d%n", typeNode.asText(), oldState, newState);
                        isUpdated = true;
                    }
                }
                // 2. 普通节点：匹配文本内容
                else {
                    JsonNode textValueNode = node.at("/text/value");
                    if (textValueNode != null && textValueNode.isTextual()) {
                        String nodeValue = textValueNode.asText().replace("\n", "");
                        if (nodeValue.contains(targetNodeName)) {
                            if (stateNode.isInt()) {
                                int oldState = stateNode.asInt();
                                ((ObjectNode) propertiesNode).put("state", newState);
                                System.out.printf("节点 [%s] 的状态已从 %d 更新为 %d%n", nodeValue, oldState, newState);
                                isUpdated = true;
                            }
                        }
                    }
                }
            }

            if (!isUpdated) {
                System.err.println("未找到与节点名称 [" + nodeName + "] 匹配的节点！");
                return null;
            }

            return objectMapper.writeValueAsString(rootNode);

        } catch (Exception e) {
            System.err.println("处理流程图 JSON 时发生异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
