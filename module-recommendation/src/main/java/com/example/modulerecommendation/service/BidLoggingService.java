package com.example.modulerecommendation.service;

import com.example.moduledomain.domain.bidLogging.BidLogging;
import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.user.Gender;
import com.example.moduledomain.repository.bidLogging.AgeRange;
import com.example.moduledomain.repository.bidLogging.BidLoggingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BidLoggingService {

    private final BidLoggingRepository bidLoggingRepository;

    public BidLoggingService(BidLoggingRepository bidLoggingRepository) {
        this.bidLoggingRepository = bidLoggingRepository;
    }

    @Transactional
    public void logging(BidLogging bidLogging) {
        bidLoggingRepository.save(bidLogging);
    }

    public Map<Integer, List<Category>> getCategoryTop2(Gender gender) {
        // 나이대 범위 정의 (예: 10대, 20대, 30대 등)
        List<AgeRange> ageRanges = Arrays.asList(
                new AgeRange(10, 19), // 10대
                new AgeRange(20, 29), // 20대
                new AgeRange(30, 39), // 30대
                new AgeRange(40, 49), // 40대
                new AgeRange(50, 59), // 50대
                new AgeRange(60, 100) // 60대 이상
        );

        return bidLoggingRepository.findTopCategoriesByGenderAndAgeRange(gender, ageRanges);
    }
}
