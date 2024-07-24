package com.ss.system.util;

public class MyObject {
    private String name;
    private int age;

    // 默认构造方法（无参数构造方法）
    public MyObject() {
    }

    // 带参数的构造方法
    public MyObject(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getter 和 Setter 方法（可以使用 IDE 自动生成）
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
