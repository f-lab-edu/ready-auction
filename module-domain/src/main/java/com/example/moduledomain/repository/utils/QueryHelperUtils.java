package com.example.moduledomain.repository.utils;

import java.util.List;
import java.util.function.Supplier;

import com.querydsl.core.types.dsl.BooleanExpression;

public class QueryHelperUtils {

    public static <T> BooleanExpression ifNotNull(T value, Supplier<BooleanExpression> expressionSupplier) {
        return value != null ? expressionSupplier.get() : null;
    }

    public static <T> BooleanExpression ifNotEmpty(List<T> value, Supplier<BooleanExpression> expressionSupplier) {
        return !value.isEmpty() ? expressionSupplier.get() : null;
    }
}
