package com.hnust.health;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hnust.health.mapper")
public class HealthAssistantApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthAssistantApplication.class, args);
    }
}
