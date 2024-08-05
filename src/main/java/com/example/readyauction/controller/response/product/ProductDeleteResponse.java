package com.example.readyauction.controller.response.product;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDeleteResponse {
	private Long id; // 업데이트 성공한 상품의 ID를 반환

	private ProductDeleteResponse(Long id) {
		this.id = id;
	}

	public ProductDeleteResponse from(Long id) {
		return new ProductDeleteResponse(id);
	}
}
