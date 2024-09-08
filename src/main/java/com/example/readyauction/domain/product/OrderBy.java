package com.example.readyauction.domain.product;

import com.querydsl.core.types.OrderSpecifier;

public enum OrderBy {
    START_DATE("시작일", product -> product.startDate.asc()),
    LIKE("좋아요", product -> null), // 좋아요 수 기준 (추후 구현),
    START_PRICE("시작가", product -> product.startPrice.asc());

    private final String description;
    private final OrderSpecifierProvider orderSpecifierProvider;

    OrderBy(String description, OrderSpecifierProvider orderSpecifierProvider) {
        this.description = description;
        this.orderSpecifierProvider = orderSpecifierProvider;
    }

    public String getDescription() {
        return description;
    }

    public OrderSpecifier<?> toOrderSpecifier(QProduct product) {
        return orderSpecifierProvider.getOrderSpecifier(product);
    }

    @FunctionalInterface
    private interface OrderSpecifierProvider {
        OrderSpecifier<?> getOrderSpecifier(QProduct product);
    }
}
