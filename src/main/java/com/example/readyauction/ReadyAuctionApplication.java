package com.example.readyauction;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class ReadyAuctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadyAuctionApplication.class, args);
	}

}
