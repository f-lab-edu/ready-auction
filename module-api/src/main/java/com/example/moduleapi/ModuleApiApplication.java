package com.example.moduleapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.example.*"})
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class ModuleApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleApiApplication.class, args);
    }

}
