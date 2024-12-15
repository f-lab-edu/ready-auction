package com.example.modulerecommendation.service;

import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.user.Gender;
import com.example.moduledomain.domain.user.User;
import com.example.moduledomain.repository.product.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductRecommendationService {
    private final BidLoggingService bidLoggingService;
    private final ProductRepository productRepository;
    private static final List<Gender> GENDERS = Arrays.asList(Gender.MALE, Gender.FEMALE);
    private static final List<String> AGE_RANGES = Arrays.asList("10", "20", "30", "40", "50", "60");
    // 성별 -> 나이대 -> 상품ID 목록(나이대별 Top2 카테고리의 상품리스트)
    private final Map<Gender, Map<String, List<Long>>> recommendationProductStore = new HashMap<>();

    public ProductRecommendationService(BidLoggingService bidLoggingService, ProductRepository productRepository) {
        this.bidLoggingService = bidLoggingService;
        this.productRepository = productRepository;
        initializeRecommendationProductStore();
    }


    // HashMap 초기화 메서드
    private void initializeRecommendationProductStore() {
        for (Gender genderType : GENDERS) {
            recommendationProductStore.put(genderType, new HashMap<>());
            for (String ageRangeKey : AGE_RANGES) {
                recommendationProductStore.get(genderType).put(ageRangeKey, new ArrayList<>());
            }
        }
        updateRecommendationProductStore();
    }

    @Transactional(readOnly = true)
    public List<Long> getRecommendationProducts(User user, int pageNo, int pageSize) {
        Gender gender = user.getGender();
        String age = String.valueOf((user.getAge() / 10) * 10);
        List<Long> recommendationProducts = recommendationProductStore.get(gender).get(age);
        Collections.shuffle(recommendationProducts);

        int totalSize = recommendationProducts.size();
        int startIndex = pageNo * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalSize);

        if (startIndex >= totalSize) {
            return new ArrayList<>();
        }
        return recommendationProducts.subList(startIndex, endIndex);
    }

    @Scheduled(cron = "0 27 3 * * THU")
    // 스케줄러로 실행될때 HashMap에 접근하는 경우 동시성 이슈가 생길 수 있는가..?
    public void updateRecommendationProductStore() {
        Map<Integer, List<Category>> maleCategoryTop2 = bidLoggingService.getCategoryTop2(Gender.MALE);
        saveInRecommendationProductStore(Gender.MALE, maleCategoryTop2);

        Map<Integer, List<Category>> femaleCategoryTop2 = bidLoggingService.getCategoryTop2(Gender.FEMALE);
        saveInRecommendationProductStore(Gender.FEMALE, femaleCategoryTop2);
    }

    private void saveInRecommendationProductStore(Gender gender, Map<Integer, List<Category>> genderCategories) {
        for (Map.Entry<Integer, List<Category>> entry : genderCategories.entrySet()) {
            Integer ageRange = entry.getKey();
            List<Category> categories = entry.getValue();
            List<Long> productIds = new ArrayList<>();
            for (Category category : categories) {
                List<Product> products = productRepository.findByCategory(category);
                List<Long> categoryProductIds = products.stream().map(Product::getId).collect(Collectors.toList());
                productIds.addAll(categoryProductIds);
            }
            recommendationProductStore.get(gender).get(ageRange.toString()).addAll(productIds);
        }
    }
}
