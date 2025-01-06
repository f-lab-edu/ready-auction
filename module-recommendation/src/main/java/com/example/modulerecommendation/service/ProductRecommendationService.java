package com.example.modulerecommendation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.moduledomain.domain.bidLogging.BidLogging;
import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.domain.user.Gender;
import com.example.moduledomain.repository.bidLogging.BidLoggingRepository;
import com.example.moduledomain.repository.product.ProductRepository;
import com.example.modulerecommendation.controller.response.ProductFindResponse;

@Service
public class ProductRecommendationService {
    private final ProductListingService productListingService;
    private final BidLoggingRepository bidLoggingRepository;
    private final ProductRepository productRepository;
    /*
     * 성별 -> 나이대별 -> 카테고리별 -> 경매 상태 -> productId
     * */
    private final Map<Gender, Map<String, Map<Category, Map<ProductCondition, List<Long>>>>> recommendationProductStore = new HashMap<>();

    public ProductRecommendationService(ProductListingService productListingService,
                                        BidLoggingRepository bidLoggingRepository,
                                        ProductRepository productRepository) {
        this.productListingService = productListingService;
        this.bidLoggingRepository = bidLoggingRepository;
        this.productRepository = productRepository;
    }

    public List<ProductFindResponse> getRecommendationProducts(Gender gender,
                                                               int age,
                                                               String keyword,
                                                               List<Category> categories,
                                                               List<ProductCondition> productConditions) {
        /*
         * 1. 카테고리별 필터
         * 1.1 필터링한 카테고리 상품이 추천 상품 카테고리에 없으면 빈 리스트 반환
         * 2. productCondition 필터
         * 2.1 ProductConditon 필터링시 상품이 없으면 빈 리스트 반환
         * 3. keyword 필터링은 ProductListingService에서 진행
         * 3. 최종적인 ProductIds 조회 후 반환.
         * */
        String ageGroup = AgeGroup.fromAge(age);
        List<Long> recommendationProductIds = new ArrayList<>(); // 최종 추천 상품 ID 리스트

        for (Category category : categories) {
            Map<Category, Map<ProductCondition, List<Long>>> categoryMap = recommendationProductStore.
                get(gender).
                get(ageGroup);

            if (categoryMap == null || categoryMap.isEmpty()) {
                // 해당 성별과 나이대에 카테고리 상품이 없다면 빈 리스트 반환
                return Collections.emptyList();
            }

            // 카테고리별 상품 상태
            Map<ProductCondition, List<Long>> productConditionListMap = categoryMap.get(category);
            if (productConditionListMap == null || productConditionListMap.isEmpty()) {
                return Collections.emptyList();
            }

            for (ProductCondition productCondition : productConditions) {
                List<Long> productIds = productConditionListMap.get(productCondition);
                recommendationProductIds.addAll(productIds);
            }
        }

        // 최종적으로 추천된 상품이 없다면 빈 리스트 반환
        if (recommendationProductIds.isEmpty()) {
            return Collections.emptyList();
        }
        return productListingService.findRecommendationProducts(recommendationProductIds, keyword);
    }

    @Scheduled(cron = "0 0 3 * * THU")
    // 스케줄러로 실행될때 HashMap에 접근하는 경우 동시성 이슈가 생길 수 있는가..?
    public void updateRecommendationProductStore() {
        saveInRecommendationProductStore(Gender.MALE, getTop2CategoriesByAgeGroup(Gender.MALE));
        saveInRecommendationProductStore(Gender.FEMALE, getTop2CategoriesByAgeGroup(Gender.FEMALE));
    }

    private Map<String, Map<Category, Long>> getCategoryCountByGenderAndAgeGroup(Gender gender) {
        List<BidLogging> loggings = bidLoggingRepository.findByGender(gender);

        Map<String, Map<Category, Long>> result = loggings.stream()
            .collect(
                Collectors.groupingBy(bidLogging -> AgeGroup.fromAge(bidLogging.getAge()),
                    Collectors.groupingBy(bidLogging -> bidLogging.getCategory(), Collectors.counting())
                ));

        return result;
    }

    private Map<String, List<Category>> getTop2CategoriesByAgeGroup(Gender gender) {
        Map<String, Map<Category, Long>> categoryCounts = getCategoryCountByGenderAndAgeGroup(gender);

        Map<String, List<Category>> topCategoriesByAgeGroup = new HashMap<>();

        // 각 나이대별로 최상위 2개 카테고리를 추출
        for (Map.Entry<String, Map<Category, Long>> entry : categoryCounts.entrySet()) {
            String ageGroup = entry.getKey();
            Map<Category, Long> categoryCountMap = entry.getValue();

            // 카테고리 개수 내림차순 정렬 후, 최상위 2개 카테고리 추출
            List<Category> topCategories = categoryCountMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))  // 내림차순 정렬
                .limit(2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            topCategoriesByAgeGroup.put(ageGroup, topCategories);
        }

        return topCategoriesByAgeGroup;
    }

    private void saveInRecommendationProductStore(Gender gender, Map<String, List<Category>> genderCategories) {
        for (Map.Entry<String, List<Category>> entry : genderCategories.entrySet()) {
            String ageGroup = entry.getKey();
            List<Category> categories = entry.getValue();

            for (Category category : categories) {
                List<Product> products = productRepository.findByCategory(category);

                for (Product product : products) {
                    recommendationProductStore
                        .computeIfAbsent(gender, g -> new HashMap<>()) // 성별 키가 없으면 새로 생성
                        .computeIfAbsent(ageGroup, a -> new HashMap<>()) // 나이대별 키가 없으면 새로 생성
                        .computeIfAbsent(category, c -> new HashMap<>()) // 카테고리별 키가 없으면 새로 생성
                        .computeIfAbsent(product.getProductCondition(), c -> new ArrayList<>()) // 카테고리별 키가 없으면 새로 생성
                        .add(product.getId()); // 해당 카테고리의 productId 리스트에 추가
                }
            }
        }
    }
}
