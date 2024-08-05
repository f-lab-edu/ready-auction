package com.example.readyauction.controller.request.product;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {
	private String productName;
	private String description;
	private LocalDateTime startDate;
	private LocalDateTime closeDate;
	private int startPrice;
}
