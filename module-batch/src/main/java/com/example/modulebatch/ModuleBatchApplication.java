package com.example.modulebatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableBatchProcessing
@ComponentScan(basePackages = {"com.example.modulebatch", "com.example.moduledomain", "com.example.moduleapi.service", "com.example.moduleapi.config"})
public class ModuleBatchApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ModuleBatchApplication.class);
        // 웹 애플리케이션 타입을 NONE으로 설정하여 웹 환경을 비활성화
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        // Spring Boot 애플리케이션 실행, args는 커맨드 라인 인자
        ConfigurableApplicationContext context = springApplication.run(args);
        // 애플리케이션 종료 후 반환할 exitCode를 설정
        int exitCode = SpringApplication.exit(context);
        // 애플리케이션 종료 코드(exitCode)로 시스템 종료
        System.exit(exitCode);
    }
}
