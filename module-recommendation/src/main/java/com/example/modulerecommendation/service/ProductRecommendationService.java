package com.example.modulerecommendation.service;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.example.moduledomain.domain.user.Gender;
import com.example.moduledomain.repository.bidLogging.BidLoggingRepository;
import com.example.moduledomain.repository.product.ProductRepository;
import com.example.modulerecommendation.controller.response.ProductFindResponse;

@Service
public class ProductRecommendationService {
    private final ProductListingService productListingService;
    private final BidLoggingRepository bidLoggingRepository;
    private final ProductRepository productRepository;

    private final Map<Gender, Map<String, List<Long>>> recommendationProductStore = new HashMap<>();
    private static final List<Gender> GENDERS = Arrays.asList(Gender.MALE, Gender.FEMALE);
    private static final List<String> AGE_RANGES = Arrays.asList("10", "20", "30", "40", "50", "60");

    public ProductRecommendationService(ProductListingService productListingService,
        BidLoggingRepository bidLoggingRepository, ProductRepository productRepository) {
        this.productListingService = productListingService;
        this.bidLoggingRepository = bidLoggingRepository;
        this.productRepository = productRepository;
        initializeRecommendationProductStore();
    }

    public List<ProductFindResponse> getRecommendationProducts(Gender gender, int age, int pageNo,
        int pageSize) {
        String ageGroup = getAgeGroup(age);
        List<Long> recommendationProducts = recommendationProductStore.get(gender).get(ageGroup);

        int totalSize = recommendationProducts.size();
        int startIndex = pageNo * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalSize);

        if (startIndex >= totalSize) {
            return Collections.emptyList();
        }

        return productListingService.findRecommendationProducts(recommendationProducts.subList(startIndex, endIndex));
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
                Collectors.groupingBy(bidLogging -> getAgeGroup(bidLogging.getAge()),
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

    private void initializeRecommendationProductStore() {
        for (Gender genderType : GENDERS) {
            recommendationProductStore.put(genderType, new HashMap<>());
            for (String ageRangeKey : AGE_RANGES) {
                recommendationProductStore.get(genderType).put(ageRangeKey, new ArrayList<>());
            }
        }
        updateRecommendationProductStore();
    }

    private void saveInRecommendationProductStore(Gender gender, Map<String, List<Category>> genderCategories) {
        for (Map.Entry<String, List<Category>> entry : genderCategories.entrySet()) {
            String ageGroup = entry.getKey();
            List<Category> categories = entry.getValue();
            List<Long> productIds = new ArrayList<>();
            for (Category category : categories) {
                List<Product> products = productRepository.findByCategory(category);
                List<Long> categoryProductIds = products.stream().map(Product::getId).collect(Collectors.toList());
                productIds.addAll(categoryProductIds);
            }
            recommendationProductStore.get(gender).get(ageGroup).addAll(productIds);
        }
    }

    private String getAgeGroup(int age) {
        if (age >= 10 && age <= 19) {
            return "10";
        } else if (age >= 20 && age <= 29) {
            return "20";
        } else if (age >= 30 && age <= 39) {
            return "30";
        } else if (age >= 40 && age <= 49) {
            return "40";
        } else if (age >= 50 && age <= 59) {
            return "50";
        } else {
            return "60";
        }
    }
}
