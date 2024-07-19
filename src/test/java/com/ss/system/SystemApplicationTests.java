package com.ss.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class SystemApplicationTests {

    //关于一些 Stream 流操作

    @Test
    void contextLoads() {
    }

    /**
     * 演示 map 的用途：一对一转换
     *
     * 得到的结果是: [User(id=205), User(id=105), User(id=308), User(id=469), User(id=627), User(id=193), User(id=111)]
     */
    @Test
    public void stringToIntMap() {
        List<String> ids = Arrays.asList("205", "105", "308", "469", "627", "193", "111");
        // 使用流操作
        List<User> results = ids.stream()
                .map(id -> {
                    User user = new User(Integer.valueOf(id));
                    return user;
                })
                .collect(Collectors.toList());
        System.out.println(results);
    }


    /**
     * 演示map的用途：一对多转换
     *
     * 结果：[hello, world, Jia, Gou, Wu, Dao]
     */
    @Test
    public void stringToIntFlatmap() {
        List<String> sentences = Arrays.asList("hello world", "Jia Gou Wu Dao");
        // 使用流操作
        List<String> results = sentences.stream()
                .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
                .collect(Collectors.toList());
        System.out.println(results);
    }





    /**
     * filter、sorted、distinct、limit
     *
     * 结果: [User(id=111), User(id=193), User(id=205)]
     */
    @Test
    public void testGetTargetUsers() {
        List<String> ids = Arrays.asList("205", "10", "308", "49", "627", "193", "111", "193");
        // 使用流操作
        List<User> results = ids.stream()
                .filter(s -> s.length() > 2)
                .distinct()
                .map(Integer::valueOf)
                .sorted(Comparator.comparingInt(o -> o))
                .limit(3)
                .map(id -> new User(id))
                .collect(Collectors.toList());
        System.out.println(results);
    }




    /**
     * 简单结果终止方法
     *
     * 结果：
     * 6
     * true
     * findFirst:205
     */
    @Test
    public void testSimpleStopOptions() {
        List<String> ids = Arrays.asList("205", "10", "308", "49", "627", "193", "111", "193");
        // 统计stream操作后剩余的元素个数
        System.out.println(ids.stream().filter(s -> s.length() > 2).count());
        // 判断是否有元素值等于205
        System.out.println(ids.stream().filter(s -> s.length() > 2).anyMatch("205"::equals));
        // findFirst操作
        ids.stream().filter(s -> s.length() > 2)
                .findFirst()
                .ifPresent(s -> System.out.println("findFirst:" + s));
    }





    /**
     * 一旦一个Stream被执行了终止操作之后，后续便不可以再读这个流执行其他的操作了，否则会报错
     */
    @Test
    public void testHandleStreamAfterClosed() {
        List<String> ids = Arrays.asList("205", "10", "308", "49", "627", "193", "111", "193");
        Stream<String> stream = ids.stream().filter(s -> s.length() > 2);
        // 统计stream操作后剩余的元素个数
        System.out.println(stream.count());
        System.out.println("-----下面会报错-----");
        // 判断是否有元素值等于205
        try {
            System.out.println(stream.anyMatch("205"::equals));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("-----上面会报错-----");
    }


    /**
     * 生成集合
     *
     * 结果：collectList:[User(id=22), User(id=23)]
     *      collectSet:[User(id=22), User(id=23)]
     *      collectMap:{22=User(id=22), 23=User(id=23)}
     */
    @Test
    public void testCollectStopOptions() {
        List<User> ids = Arrays.asList(new User(17), new User(22), new User(23));
        // collect成list
        List<User> collectList = ids.stream().filter(dept -> dept.getId() > 20)
                .collect(Collectors.toList());
        System.out.println("collectList:" + collectList);
        // collect成Set
        Set<User> collectSet = ids.stream().filter(dept -> dept.getId() > 20)
                .collect(Collectors.toSet());
        System.out.println("collectSet:" + collectSet);
        // collect成HashMap，key为id，value为Dept对象
        Map<Integer, User> collectMap = ids.stream().filter(dept -> dept.getId() > 20)
                .collect(Collectors.toMap(User::getId, dept -> dept));
        System.out.println("collectMap:" + collectMap);
    }




    /**
     * 生成拼接字符串
     *
     * 结果：拼接后：205,10,308,49,627,193,111,193
     */
    @Test
    public void testCollectJoinStrings() {
        List<String> ids = Arrays.asList("205", "10", "308", "49", "627", "193", "111", "193");
        String joinResult = ids.stream().collect(Collectors.joining(","));
        System.out.println("拼接后：" + joinResult);
    }

    /**
     * 数学运算
     */
    @Test
    public void testNumberCalculate() {
        List<Integer> ids = Arrays.asList(10, 20, 30, 40, 50);
        // 计算平均值
        Double average = ids.stream().collect(Collectors.averagingInt(value -> value));
        System.out.println("平均值：" + average);
        // 数据统计信息
        IntSummaryStatistics summary = ids.stream().collect(Collectors.summarizingInt(value -> value));
        System.out.println("数据统计信息： " + summary);
    }
}


@Data
@AllArgsConstructor
class User {
    private int id;
}


