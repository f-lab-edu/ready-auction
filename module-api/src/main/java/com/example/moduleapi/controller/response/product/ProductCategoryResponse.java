package com.example.moduleapi.controller.response.product;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCategoryResponse {
    private String name;
    private String description;

    public ProductCategoryResponse(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
