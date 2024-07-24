package com.ss.system.util;

import com.fasterxml.jackson.databind.ObjectMapper;
 
// JsonFormatter 类负责管理 ObjectMapper 对象，它是 Jackson 库中用于处理 JSON 的核心类。
 
public class JsonFormatter {
    private static volatile JsonFormatter instance;
    private ObjectMapper objectMapper;
 
    // 私有构造方法，防止外部实例化
    private JsonFormatter() {
        // 初始化 ObjectMapper
        objectMapper = new ObjectMapper();
        // 可以在这里配置 ObjectMapper 的特性，例如日期格式化、空字段处理等
    }
 
    // 获取单例实例的静态方法
// 使用双检锁（double-checked locking）来确保在多线程环境下只创建一个 JsonFormatter 实例。
    public static JsonFormatter getInstance() {
        if (instance == null) {
//volatile 关键字确保在多线程环境中正确地处理 instance 变量，防止指令重排序带来的问题。
            synchronized (JsonFormatter.class) {
                if (instance == null) {
                    instance = new JsonFormatter();
                }
            }
        }
        return instance;
    }
 
    // 提供了一个公共方法来格式化 JSON 字符串，可以将对象转换为 JSON 格式的字符串。
    public String formatJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}