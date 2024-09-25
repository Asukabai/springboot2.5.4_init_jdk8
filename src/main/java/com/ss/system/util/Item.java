package com.ss.system.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// 可序列化的Item类
class Item implements Serializable {
    String name;

    Item(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}

