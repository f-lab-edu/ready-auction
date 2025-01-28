package com.example.moduledomain.common.request;

import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.ProductCondition;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProductFilter {
    private String keyword;
    private List<ProductCondition> productCondition = new ArrayList<>();
    private List<Category> category = new ArrayList<>();

    @Builder
    public ProductFilter(String keyword, List<ProductCondition> productCondition, List<Category> category) {
        this.keyword = keyword;
        this.productCondition = productCondition;
        this.category = category;
    }
}
