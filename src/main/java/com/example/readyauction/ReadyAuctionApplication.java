package com.example.readyauction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ReadyAuctionApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReadyAuctionApplication.class, args);
  }

}
