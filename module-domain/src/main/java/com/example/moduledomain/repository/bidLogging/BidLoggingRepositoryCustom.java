package com.example.moduledomain.repository.bidLogging;

import com.example.moduledomain.domain.user.Gender;

import java.util.List;

public interface BidLoggingRepositoryCustom {
    /**
     * 성별에 따라 입찰 참여자 수 기준으로 입찰 많은 상품 Top10 반환
     */
    List<Long> findTop10ProductIdsByGender(Gender gender);
}
