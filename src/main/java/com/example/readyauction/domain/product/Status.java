package com.example.readyauction.domain.product;

public enum Status {
    READY("준비중"), ACTIVE("진행중"), DONE("종료");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
