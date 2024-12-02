package com.example.moduleapi.service.product;

import com.example.moduleapi.controller.response.product.ProductLikeResponse;
import com.example.moduledomain.domain.user.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class ProductLikeService {

    private final RedisTemplate<Long, Long> redisTemplate;

    public ProductLikeService(RedisTemplate<Long, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * setOperations 1번째 : key = productId, value = 자료구조Set,  userId 저장 : 상품 좋아요 개수 계산 목적
     * setOperations 2번째 : key = userId, value = 자료구조Set,  productId 저장 : 유저별 좋아요 누른 상품 조회 목적
     */
    @Transactional
    public ProductLikeResponse addLike(User user, Long productId) {
        SetOperations<Long, Long> setOperations = redisTemplate.opsForSet();
        setOperations.add(productId, user.getId());
        setOperations.add(user.getId(), productId);
        return new ProductLikeResponse(countProductLikesByProductId(productId));
    }

    @Transactional
    public ProductLikeResponse deleteLike(User user, Long productId) {
        SetOperations<Long, Long> setOperations = redisTemplate.opsForSet();
        setOperations.remove(productId, user.getId());
        setOperations.remove(user.getId(), productId);
        return new ProductLikeResponse(countProductLikesByProductId(productId));
    }

    @Transactional(readOnly = true)
    public ProductLikeResponse getProductLikesByProductId(Long productId) {
        return new ProductLikeResponse(countProductLikesByProductId(productId));
    }

    @Transactional(readOnly = true)
    public List<Long> getUsersByProductId(Long productId) {
        Set<Long> userIds = redisTemplate.opsForSet().members(productId);
        // null이 아닌 빈 Set을 반환
        if (userIds == null) {
            userIds = Collections.emptySet();
        }
        return new ArrayList<>(userIds);
    }

    @Transactional
    public int countProductLikesByProductId(Long productId) {
        Long count = redisTemplate.opsForSet().size(productId);
        return count == null ? 0 : count.intValue();
    }

    @Transactional
    public Set<Long> getLikedProductListByUserId(Long userId) {
        Set<Long> productIds = redisTemplate.opsForSet().members(userId);
        return productIds;
    }
}
