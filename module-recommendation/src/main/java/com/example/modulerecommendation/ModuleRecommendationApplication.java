package com.example.modulerecommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.example.moduledomain"})
@EnableJpaRepositories(basePackages = "com.example.moduledomain")
@EntityScan(basePackages = "com.example.moduledomain")
public class ModuleRecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleRecommendationApplication.class, args);
    }

}
