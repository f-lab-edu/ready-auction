package com.example.moduledomain.repository.product;

import static com.example.moduledomain.domain.product.QProduct.*;
import static com.example.moduledomain.domain.product.QProductLike.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.OrderBy;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.repository.utils.QueryHelperUtils;
import com.example.moduledomain.request.ProductFilterRequest;
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
    public List<Product> findProductsWithCriteria(ProductFilterRequest productFilterRequest) {

        String keyword = productFilterRequest.getProductFilter().getKeyword();
        List<Category> categories = productFilterRequest.getProductFilter().getCategory();
        List<ProductCondition> productConditions = productFilterRequest.getProductFilter().getProductCondition();
        OrderBy order = productFilterRequest.getOrderBy();
        int pageNo = productFilterRequest.getPageNo();
        int pageSize = productFilterRequest.getPageSize();

        OrderSpecifier<?> orderSpecifier = order.toOrderSpecifier();

        if (order == OrderBy.LIKE) {
            NumberPath<Long> like = Expressions.numberPath(Long.class, "like");
            List<Tuple> result = jpaQueryFactory
                .select(product, productLike.id.count().as(like))
                .from(product)
                .leftJoin(productLike).on(product.id.eq(productLike.productId))
                .where(containsKeyword(keyword),
                       filterProductCondition(productConditions),
                       filterCategories(categories))
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
                   filterProductCondition(productConditions),
                   filterCategories(categories))
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

    public BooleanExpression filterProductCondition(List<ProductCondition> productConditions) {
        LocalDateTime currentTime = LocalDateTime.now();
        product.startDate.after(currentTime).or(product.startDate.loe(currentTime)
                                                    .and(product.closeDate.after(currentTime)));
        if (!productConditions.isEmpty()) {
            return product.productCondition.in(productConditions);
        }
        //상태가 null인 경우 기본적으로 READY와 ACTIVE 상태 필터링
        return product.startDate.after(currentTime).or(product.startDate.loe(currentTime)
                                                           .and(product.closeDate.after(currentTime)));
    }

    public BooleanExpression filterCategories(List<Category> categories) {
        return QueryHelperUtils.ifNotEmpty(categories, () -> product.category.in(categories));
    }
}
