package com.example.moduleapi.controller.response.product;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductLikeResponse {
    private int likesCount;

    public ProductLikeResponse(int likesCount) {
        this.likesCount = likesCount;
    }
}
