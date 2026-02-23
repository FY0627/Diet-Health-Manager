package com.fanchenyi.diet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fanchenyi.diet.mapper")
public class DietHealthManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietHealthManagerApplication.class, args);
    }

}
