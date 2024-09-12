package com.example.readyauction.repository;

import static com.example.readyauction.domain.product.QProduct.*;
import static com.example.readyauction.domain.product.QProductLike.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.readyauction.domain.product.OrderBy;
import com.example.readyauction.domain.product.Product;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ProductRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Product> findProductsWithCriteria(String keyword, int pageNo, int pageSize, OrderBy order) {
        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = order != null ? order.toOrderSpecifier() : product.id.desc();

        if (order == OrderBy.LIKE) {
            List<Product> products = jpaQueryFactory.selectFrom(product)
                .leftJoin(productLike).on(product.id.eq(productLike.productId))
                .where(containsKeyword(keyword))
                .groupBy(product.id)
                .orderBy(orderSpecifier)
                .offset(pageNo * pageSize)
                .limit(pageSize)
                .fetch();
            return products;

        }

        List<Product> products = jpaQueryFactory
            .selectFrom(product)
            .where(containsKeyword(keyword))
            .orderBy(orderSpecifier)
            .offset(pageNo * pageSize)
            .limit(pageSize)
            .fetch();

        return products;
    }

    private BooleanExpression containsKeyword(String keyword) {
        return keyword != null ? product.productName.containsIgnoreCase(keyword)
            .or(product.description.containsIgnoreCase(keyword)) : null;
    }
}
