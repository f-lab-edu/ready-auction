package com.example.moduledomain.repository.bidLogging;

import com.example.moduledomain.domain.user.Gender;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.moduledomain.domain.bidLogging.QBidLogging.bidLogging;

@Repository
public class BidLoggingRepositoryCustomImpl implements BidLoggingRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public BidLoggingRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Long> findTop10ProductIdsByGender(Gender gender) {
        return jpaQueryFactory.select(bidLogging.productId)
                              .from(bidLogging)
                              .where(bidLogging.gender.eq(gender))
                              .groupBy(bidLogging.productId)
                              .orderBy(bidLogging.userId.countDistinct().desc())
                              .limit(10)
                              .fetch();
    }
}
