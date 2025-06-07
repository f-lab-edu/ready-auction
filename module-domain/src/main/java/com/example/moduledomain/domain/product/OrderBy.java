package com.example.moduledomain.domain.product;

import com.querydsl.core.types.OrderSpecifier;

public enum OrderBy {
    LATEST("최신순") {
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return QProduct.product.id.desc();
        }
    },
    START_DATE("시작일") {
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return QProduct.product.startDate.asc();
        }
    },
    CLOSE_DATE("종료일 임박") {
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return QProduct.product.closeDate.asc();
        }
    },
    LIKE("좋아요") {
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return QProductLike.productLike.id.count().desc();
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
