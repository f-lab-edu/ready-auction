package com.example.readyauction.repository;

import static com.example.readyauction.domain.product.QProduct.*;
import static com.example.readyauction.domain.product.QProductLike.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.readyauction.domain.product.OrderBy;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.Status;
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
    public List<Product> findProductsWithCriteria(String keyword, Status status, int pageNo, int pageSize,
        OrderBy order) {
        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = order != null ? order.toOrderSpecifier() : product.id.desc();

        if (order == OrderBy.LIKE) {
            List<Product> products = jpaQueryFactory.selectFrom(product)
                .leftJoin(productLike).on(product.id.eq(productLike.productId))
                .where(containsKeyword(keyword),
                    filterProductStatus(status))
                .groupBy(product.id)
                .orderBy(orderSpecifier)
                .offset(pageNo * pageSize)
                .limit(pageSize)
                .fetch();
            return products;

        }

        List<Product> products = jpaQueryFactory
            .selectFrom(product)
            .where(containsKeyword(keyword),
                filterProductStatus(status))
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

    private BooleanExpression filterProductStatus(Status status) {
        LocalDateTime requestTime = LocalDateTime.now();
        if (status != null) {
            switch (status) {
                case READY:
                    return product.startDate.after(requestTime);
                case ACTIVE:
                    return product.startDate.loe(requestTime)
                        .and(product.closeDate.after(requestTime));
                case DONE:
                    return product.closeDate.loe(requestTime);
            }
        }
        //상태가 null인 경우 기본적으로 READY와 ACTIVE 상태 필터링
        return product.startDate.after(requestTime).or(product.startDate.loe(requestTime)
            .and(product.closeDate.after(requestTime)));
    }
}
