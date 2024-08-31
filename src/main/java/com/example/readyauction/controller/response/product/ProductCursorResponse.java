package com.example.readyauction.controller.response.product;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCursorResponse {
	private List<ProductFindResponse> products;
	private Long nextCursorId;
	private Boolean hasNext;

	public ProductCursorResponse(List<ProductFindResponse> products, Long nextCursorId, Boolean hasNext) {
		this.products = products;
		this.nextCursorId = nextCursorId;
		this.hasNext = hasNext;
	}

	public static ProductCursorResponse from(List<ProductFindResponse> products, Long nextCursorId, Boolean hasNext) {
		return new ProductCursorResponse(products, nextCursorId, hasNext);
	}

}
