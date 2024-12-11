package com.example.modulerecommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.example.*"})
@EnableScheduling
public class ModuleRecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleRecommendationApplication.class, args);
    }

}
