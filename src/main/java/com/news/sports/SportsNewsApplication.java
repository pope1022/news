package com.news.sports;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.news.sports.mapper")
public class SportsNewsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SportsNewsApplication.class, args);
    }
} 