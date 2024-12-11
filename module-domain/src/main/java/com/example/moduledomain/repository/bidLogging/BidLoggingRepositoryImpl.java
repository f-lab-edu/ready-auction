package com.example.moduledomain.repository.bidLogging;

import com.example.moduledomain.domain.bidLogging.QBidLogging;
import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.user.Gender;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BidLoggingRepositoryImpl implements BidLoggingRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public BidLoggingRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Map<Integer, List<Category>> findTopCategoriesByGenderAndAgeRange(Gender gender, List<AgeRange> ageRanges) {
        Map<Integer, List<Category>> topCategoriesMap = new HashMap<>();

        // 각 나이대에 대해 상위 2개 카테고리를 가져오기
        for (AgeRange range : ageRanges) {
            BooleanExpression ageCondition = QBidLogging.bidLogging.age.between(range.getMinAge(), range.getMaxAge());

            // 각 나이대 상위 2개 카테고리 가져오기
            List<Tuple> result = jpaQueryFactory.select(QBidLogging.bidLogging.category, QBidLogging.bidLogging.count())
                    .from(QBidLogging.bidLogging)
                    .where(QBidLogging.bidLogging.gender.eq(gender).and(ageCondition))
                    .groupBy(QBidLogging.bidLogging.category)
                    .orderBy(QBidLogging.bidLogging.count().desc())
                    .limit(2)
                    .fetch();

            // 각 나이대의 상위 2개 카테고리 리스트로 변환
            List<Category> topCategories = result.stream()
                    .map(tuple -> tuple.get(QBidLogging.bidLogging.category))
                    .collect(Collectors.toList());

//            {
//                10: [Electronics, Fashion],    // 10대 상위 카테고리
//                20: [Sports, Beauty],          // 20대 상위 카테고리
//                30: [Home Appliances, Fitness] // 30대 상위 카테고리
//            }
            topCategoriesMap.put(range.getIntegerAge(), topCategories);
        }

        return topCategoriesMap;
    }


}
