package com.pai.omc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 协议自动识别系统-云端OMC工具启动类
 *
 * @author make java
 * @since 2026-03-08
 */
@SpringBootApplication
@EnableScheduling
public class OmcApplication {
    public static void main(String[] args) {
        SpringApplication.run(OmcApplication.class, args);
    }
}
