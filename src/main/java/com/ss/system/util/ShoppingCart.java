package com.ss.system.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 可序列化的ShoppingCart类
class ShoppingCart implements Serializable {
    List<Item> items = new ArrayList<>();

    void addItem(Item item) {
        items.add(item);
    }

    @Override
    public String toString() {
        return "ShoppingCart{" + "items=" + items + '}';
    }
}