package com.example.moduleapi.service.product;

import com.example.moduleapi.controller.response.product.ProductLikeResponse;
import com.example.moduledomain.domain.user.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProductLikeService {
    private final RedisTemplate<String, Long> redisTemplate;
    private final String USER_REDIS_PREFIX = "user";
    private final String PRODUCT_REDIS_PREFIX = "product";


    public ProductLikeService(@Qualifier("likeRedisTemplate") RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public ProductLikeResponse addLike(User user, Long productId) {
        String userKey = createRedisKey(USER_REDIS_PREFIX, user.getId());
        String productKey = createRedisKey(PRODUCT_REDIS_PREFIX, productId);
        int likeCount = countProductLikesByProductId(productId);

        if (!alreadyLike(userKey, productId)) {
            double score = getCurrentTimeInSeconds();
            redisTemplate.opsForZSet().add(userKey, productId, score);
            redisTemplate.opsForValue().increment(productKey);
            likeCount++;
        }
        return new ProductLikeResponse(likeCount);
    }

    @Transactional
    public ProductLikeResponse deleteLike(User user, Long productId) {
        String userKey = createRedisKey(USER_REDIS_PREFIX, user.getId());
        String productKey = createRedisKey(PRODUCT_REDIS_PREFIX, productId);
        int likeCount = countProductLikesByProductId(productId);

        if (alreadyLike(userKey, productId)) {
            redisTemplate.opsForZSet().remove(userKey, productId);
            redisTemplate.opsForValue().decrement(productKey);
            likeCount--;
        }
        return new ProductLikeResponse(likeCount);
    }

    public List<Long> getLikeProductsByUser(User user) {
        String userKey = createRedisKey(USER_REDIS_PREFIX, user.getId());
        Set<Long> productIds = redisTemplate.opsForZSet().reverseRange(userKey, 0, 1);

        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        return new ArrayList<>(productIds);
    }

    public int countProductLikesByProductId(Long productId) {
        String productKey = createRedisKey(PRODUCT_REDIS_PREFIX, productId);
        Long count = redisTemplate.opsForValue().get(productKey);
        return count == null ? 0 : count.intValue();
    }

    private String createRedisKey(String domain, Long id) {
        return String.format("%s:%d", domain.toLowerCase(), id);
    }

    private boolean alreadyLike(String userKey, Long productId) {
        Double score = redisTemplate.opsForZSet().score(userKey, productId);
        return score != null;
    }

    private double getCurrentTimeInSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }

}
