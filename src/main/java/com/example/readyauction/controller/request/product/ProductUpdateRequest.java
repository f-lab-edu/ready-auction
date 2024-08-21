package com.example.readyauction.controller.request.product;

import java.time.LocalDateTime;

import lombok.Builder;
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

	@Builder
	public ProductUpdateRequest(String productName, String description, LocalDateTime startDate,
		LocalDateTime closeDate,
		int startPrice) {
		this.productName = productName;
		this.description = description;
		this.startDate = startDate;
		this.closeDate = closeDate;
		this.startPrice = startPrice;
	}
}
