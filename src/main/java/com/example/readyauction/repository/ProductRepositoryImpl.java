package com.example.readyauction.repository;

import static com.example.readyauction.domain.product.QProduct.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.example.readyauction.domain.product.OrderBy;
import com.example.readyauction.domain.product.Product;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ProductRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Product> findProductsWithCriteria(String keyword, int pageNo, int pageSize, OrderBy order) {
        BooleanBuilder builder = QueryUtils.ifNotNull(keyword, product);

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = order != null ? order.toOrderSpecifier(product) : product.id.desc();

        List<Product> products = jpaQueryFactory
            .selectFrom(product)
            .where(builder)
            .orderBy(orderSpecifier)
            .offset(pageNo * pageSize)
            .limit(pageSize)
            .fetch();

        int totalCount = jpaQueryFactory
            .selectFrom(product)
            .where(builder)
            .fetch()
            .size();

        return new PageImpl<>(products, PageRequest.of(pageNo, pageSize), totalCount);
    }
}
