package com.ss.system;

import com.ss.system.util.JsonFormatter;
import com.ss.system.util.MyObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
        JsonFormatter jsonFormatter = JsonFormatter.getInstance();
        // 示例对象
        MyObject obj = new MyObject("John Doe", 30);
        // 格式化为 JSON 字符串
        String jsonString = jsonFormatter.formatJson(obj);
        System.out.println("Formatted JSON: " + jsonString);
    }
}
