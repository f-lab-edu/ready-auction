package com.example.readyauction.controller.response.product;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductResponse {
	private Long id;

	private ProductResponse(Long id) {
		this.id = id;
	}

	public static ProductResponse from(Long id) {
		return new ProductResponse(id);
	}
}
