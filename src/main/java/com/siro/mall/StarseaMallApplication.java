package com.siro.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 小米商城主启动类
 */
@EnableSwagger2
@MapperScan("com.siro.mall.dao")
@SpringBootApplication
public class StarseaMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarseaMallApplication.class, args);
    }

}
