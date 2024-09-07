package com.example.readyauction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReadyAuctionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadyAuctionApplication.class, args);
    }

}
//http://localhost:8080/api/v1/products?keyword=product&pageNo=0&orderBy=START_PRICE
