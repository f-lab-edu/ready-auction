package com.example.readyauction.domain.product;

import com.querydsl.core.types.OrderSpecifier;

public enum OrderBy {
    START_DATE("시작일") {
        public OrderSpecifier<?> toOrderSpecifier(QProduct product) {
            return product.startDate.desc(); // 시작일 기준으로 내림차순
        }
    },
    LIKE("좋아요") {
        public OrderSpecifier<?> toOrderSpecifier(QProduct product) {
            return null; // 좋아요 수 기준 (추후 구현)
        }
    },
    START_PRICE("시작가") {
        public OrderSpecifier<?> toOrderSpecifier(QProduct product) {
            return product.startPrice.asc(); // 시작가 기준으로 오름차순
        }
    };

    private final String description;

    OrderBy(String description) {
        this.description = description;
    }

    public abstract OrderSpecifier<?> toOrderSpecifier(QProduct product);

    public String getDescription() {
        return description;
    }
}
