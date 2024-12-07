package com.example.modulerecommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.*"})
public class ModuleRecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleRecommendationApplication.class, args);
    }

}
