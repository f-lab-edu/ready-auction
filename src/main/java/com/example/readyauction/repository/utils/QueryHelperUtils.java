package com.example.readyauction.repository.utils;

import java.util.function.Supplier;

import com.querydsl.core.types.dsl.BooleanExpression;

public class QueryHelperUtils {

    public static <T> BooleanExpression ifNotNull(T value, Supplier<BooleanExpression> expressionSupplier) {
        return value != null ? expressionSupplier.get() : null;
    }
}