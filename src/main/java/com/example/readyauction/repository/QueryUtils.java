package com.example.readyauction.repository;

import com.example.readyauction.domain.product.QProduct;
import com.querydsl.core.BooleanBuilder;

public class QueryUtils {
    static BooleanBuilder ifNotNull(String keyword, QProduct product) {
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword != null && !keyword.isEmpty()) {
            builder.and(product.productName.containsIgnoreCase(keyword))
                .or(product.description.containsIgnoreCase(keyword));
        }
        return builder;
    }

}
