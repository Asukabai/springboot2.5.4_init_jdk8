package com.ss.system.util;

public class DeepCopyExample {
    public static void main(String[] args) {
        // 创建一个原始的ShoppingCart对象并添加一些Item
        ShoppingCart cart1 = new ShoppingCart();
        cart1.addItem(new Item("Apple"));
        cart1.addItem(new Item("Banana"));

        // 使用深拷贝方法
        ShoppingCart cart2 = DeepCopyViaSerialization.deepCopy(cart1);

        // 修改cart2，检查cart1是否受到影响
        cart2.addItem(new Item("Orange"));

        System.out.println("Cart 1: " + cart1);
        System.out.println("Cart 2: " + cart2);

        // 输出结果
        // Cart 1: ShoppingCart{items=[Apple, Banana]}
        // Cart 2: ShoppingCart{items=[Apple, Banana, Orange]}
    }
}