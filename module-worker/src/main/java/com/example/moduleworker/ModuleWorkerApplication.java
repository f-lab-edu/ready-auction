package com.example.moduleworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.moduleapi.config"})
public class ModuleWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleWorkerApplication.class, args);
    }

}
