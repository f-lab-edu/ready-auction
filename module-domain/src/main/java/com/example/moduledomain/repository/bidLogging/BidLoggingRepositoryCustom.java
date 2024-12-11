package com.example.moduledomain.repository.bidLogging;

import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.user.Gender;

import java.util.List;
import java.util.Map;

public interface BidLoggingRepositoryCustom {
    Map<Integer, List<Category>> findTopCategoriesByGenderAndAgeRange(Gender gender, List<AgeRange> ageRanges);
}
