package com.luopc.platform.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 <a href="https://mp.weixin.qq.com/s/YPMARalQ4IRoNcexXT5JwQ">SpringBoot 整合 MongoDB 实现文档数据存储，实战讲解！</a>
 <a href="https://mp.weixin.qq.com/s/-9qGbIG9Cee6jJJSQg3k7g">Springboot整合Mongodb 一篇文章就搞定啦！</a>
 */
@Slf4j
@SpringBootApplication
public class MongodbDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MongodbDemoApplication.class, args);
    }
}
