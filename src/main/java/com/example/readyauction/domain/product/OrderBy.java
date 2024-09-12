package com.example.readyauction.domain.product;

import com.querydsl.core.types.OrderSpecifier;

public enum OrderBy {
    START_DATE("시작일") {
        @Override
        public OrderSpecifier<?> toOrderSpecifier(QProduct product) {
            return product.startDate.asc();
        }
    },
    LIKE("좋아요") {
        @Override
        public OrderSpecifier<?> toOrderSpecifier(QProduct product) {
            return product.startDate.asc();
        }
    },
    START_PRICE("시작가") {
        @Override
        public OrderSpecifier<?> toOrderSpecifier(QProduct product) {
            return product.startPrice.asc();
        }
    };

    private final String description;

    OrderBy(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract OrderSpecifier<?> toOrderSpecifier(QProduct product);
}
