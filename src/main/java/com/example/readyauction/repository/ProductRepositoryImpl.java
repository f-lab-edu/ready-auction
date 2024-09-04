package com.example.readyauction.repository;

import static com.example.readyauction.domain.product.QProduct.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.example.readyauction.domain.product.Product;
import com.example.readyauction.service.product.OrderBy;
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
        BooleanBuilder builder = new BooleanBuilder(); // 이건 의존성 주입?

        // 검색 keyword 필터링 - productName이나 description에 있는 것만 가져오기
        if (keyword != null && !keyword.isEmpty()) {
            builder.and(product.productName.containsIgnoreCase(keyword))
                .or(product.description.containsIgnoreCase(keyword));
        }

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(order);

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

    private OrderSpecifier<?> getOrderSpecifier(OrderBy sort) {
        if (sort == null) {
            return product.id.desc();
        }
        switch (sort) {
            case START_DATE:
                return product.startDate.desc(); // 시작일 기준으로 내림차순
            case LIKE:
                return null; // 좋아요 수 기준 -> 좋아요 merge 되면 추가하기
            case START_PRICE:
                return product.startPrice.asc(); // 시작가 기준으로 오름차순
            default:
                return product.id.desc(); // 기본값 : 최신순
        }
    }
}
