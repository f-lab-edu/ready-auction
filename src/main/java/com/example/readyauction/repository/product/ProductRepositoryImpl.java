package com.example.readyauction.repository.product;

import static com.example.readyauction.domain.product.QProduct.*;
import static com.example.readyauction.domain.product.QProductLike.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.readyauction.domain.product.OrderBy;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductCondition;
import com.example.readyauction.repository.utils.QueryHelperUtils;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ProductRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Product> findProductsWithCriteria(String keyword, ProductCondition productCondition, int pageNo,
        int pageSize,
        OrderBy order) {
        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = order.toOrderSpecifier();
        if (order == OrderBy.LIKE) {
            NumberPath<Long> like = Expressions.numberPath(Long.class, "like");
            List<Tuple> result = jpaQueryFactory
                .select(product, productLike.id.count().as(like))
                .from(product)
                .leftJoin(productLike).on(product.id.eq(productLike.productId))
                .where(containsKeyword(keyword),
                    filterProductCondition(productCondition))
                .groupBy(product.id)
                .orderBy(like.desc())
                .offset(pageNo * pageSize)
                .limit(pageSize)
                .fetch();

            return result.stream()
                .map(tuple -> tuple.get(product))
                .collect(Collectors.toList());
        }

        return jpaQueryFactory
            .selectFrom(product)
            .where(containsKeyword(keyword),
                filterProductCondition(productCondition))
            .orderBy(orderSpecifier)
            .offset(pageNo * pageSize)
            .limit(pageSize)
            .fetch();
    }

    public BooleanExpression containsKeyword(String keyword) {
        return QueryHelperUtils.ifNotNull(keyword,
            () -> product.productName.containsIgnoreCase(keyword)
                .or(product.description.containsIgnoreCase(keyword)));
    }

    public BooleanExpression filterProductCondition(ProductCondition productCondition) {
        LocalDateTime requestTime = LocalDateTime.now();
        if (productCondition != null) {
            return productCondition.checkCondition(requestTime);
        }
        //상태가 null인 경우 기본적으로 READY와 ACTIVE 상태 필터링
        return product.startDate.after(requestTime).or(product.startDate.loe(requestTime)
            .and(product.closeDate.after(requestTime)));
    }
}
