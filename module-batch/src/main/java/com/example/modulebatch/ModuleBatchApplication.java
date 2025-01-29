package com.example.modulebatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.example.modulebatch",
        "com.example.moduledomain",
        "com.example.moduleapi.service",
        "com.example.moduleapi.config"
})
@EnableJpaRepositories(basePackages = "com.example.moduledomain")
@EntityScan(basePackages = "com.example.moduledomain")
public class ModuleBatchApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ModuleBatchApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = springApplication.run(args);
        int exitCode = SpringApplication.exit(context);
        System.exit(exitCode);
    }
}
