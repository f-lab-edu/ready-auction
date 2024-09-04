package com.example.readyauction.service.product;

public enum OrderBy {
    START_DATE("시작일"),
    LIKE("좋아요"),
    START_PRICE("시작가");

    private final String description;

    OrderBy(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
