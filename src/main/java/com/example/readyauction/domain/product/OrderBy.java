package com.example.readyauction.domain.product;

import com.querydsl.core.types.OrderSpecifier;

public enum OrderBy {
    START_DATE("시작일") {
        public OrderSpecifier<?> toOrderSpecifier() {
            return QProduct.product.startDate.asc();
        }
    },
    LIKE("좋아요") {
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return QProductLike.productLike.count().desc();
        }
    },
    START_PRICE("시작가") {
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return QProduct.product.startPrice.asc();
        }
    };

    private final String description;

    OrderBy(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract OrderSpecifier<?> toOrderSpecifier();
}
