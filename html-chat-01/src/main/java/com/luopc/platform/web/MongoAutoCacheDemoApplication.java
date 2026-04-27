package com.luopc.platform.web;

import com.luopc.platform.cache.starter.EnableConfigCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@EnableConfigCache
@SpringBootApplication
public class MongoAutoCacheDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MongoAutoCacheDemoApplication.class, args);
    }
}
