package com.example.moduledomain.domain.product;

import static com.example.moduledomain.domain.product.QProduct.*;

import java.time.LocalDateTime;

import com.querydsl.core.types.dsl.BooleanExpression;

public enum ProductCondition {
    READY("준비중") {
        @Override
        public BooleanExpression checkCondition(LocalDateTime requestTime) {
            return product.startDate.after(requestTime);
        }

    },
    ACTIVE("진행중") {
        @Override
        public BooleanExpression checkCondition(LocalDateTime requestTime) {
            return product.startDate.loe(requestTime)
                .and(product.closeDate.after(requestTime));
        }
    },
    DONE("종료") {
        @Override
        public BooleanExpression checkCondition(LocalDateTime requestTime) {
            return product.closeDate.loe(requestTime);
        }
    };

    private final String description;

    ProductCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract BooleanExpression checkCondition(LocalDateTime requestTime);

}
