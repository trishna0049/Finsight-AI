package com.finsight.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FinSightPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinSightPlatformApplication.class, args);
    }
}
