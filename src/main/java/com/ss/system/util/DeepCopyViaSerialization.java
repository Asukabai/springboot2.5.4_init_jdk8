package com.ss.system.util;

import java.io.*;


/**
 *  序列化实现深拷贝
 *  :
 *  这个方法首先将original对象序列化到一个ByteArrayOutputStream中，然后立即使用ByteArrayInputStream从这个字节流中反序列化对象，
 *  从而得到一个完全独立的深拷贝。这种方法的优点是它相对简单，且能自动处理对象图中的所有复杂关系。然而，它可能比直接使用拷贝构造函数或者克隆方法更慢，
 *  且只有实现了Serializable接口的对象才能被复制。
 */

public class DeepCopyViaSerialization {
    // 实现深拷贝的方法
    public static <T> T deepCopy ( T original ) {
        T copied = null;
        try {
            // 创建一个字节流
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // 序列化
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(original);
            }
            // 反序列化
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
                copied = (T) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return copied;
    }
}
