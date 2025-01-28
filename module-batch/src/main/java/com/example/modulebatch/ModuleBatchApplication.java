package com.example.modulebatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.modulebatch", "com.example.moduledomain", "com.example.moduleapi.service", "com.example.moduleapi.config"})
public class ModuleBatchApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ModuleBatchApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = springApplication.run(args);
        int exitCode = SpringApplication.exit(context);
        System.exit(exitCode);
    }
}
