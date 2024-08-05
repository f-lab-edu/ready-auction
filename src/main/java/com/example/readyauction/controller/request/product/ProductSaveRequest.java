package com.example.readyauction.controller.request.product;

import java.time.LocalDateTime;

import com.example.readyauction.domain.product.Product;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSaveRequest {
	private String productName;
	private String description;
	private LocalDateTime startDate;
	private LocalDateTime closeDate;
	private int startPrice;

	public Product toEntity() {
		return Product.builder()
			.productName(productName)
			.description(description)
			.startDate(startDate)
			.closeDate(closeDate)
			.startPrice(startPrice)
			.build();
	}

}
