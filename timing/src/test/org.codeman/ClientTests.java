//package org.codeman;
//
////import org.codeman.config.RedissonConfig;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author hdgaadd
// * Created on 2022/10/02
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
////@ContextConfiguration(classes = {RedissonConfig.class})
//@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
//public class ClientTests {
//
//    @Autowired
//    RedisTemplate redisTemplate;
//
//    @Test
//    public void test() {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        valueOperations.set("redisson", "hello word");
//    }
//
//}