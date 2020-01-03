package com.atguigu.gmall.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConf {

    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.timeOut:20000}")
    private int timeOut;

    /*
    <bean name="redisUtil"  class="com.atguigu.gmall0715.config.RedisUtil">
    </bean>
     */
    @Bean
    public RedisUtil getRedisUtil(){
        // 如果没有host 则返回一个空对象
        if ("disabled".equals(host)){
            return null;
        }

        RedisUtil redisUtil = new RedisUtil();
        // 初始化连接池工厂
        redisUtil.initJedisPool(host,port,timeOut);

        return redisUtil;
    }



}
