package com.example.moduleapi.service.product;

import com.example.moduleapi.controller.response.product.ProductLikeResponse;
import com.example.moduledomain.domain.user.User;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public ProductLikeService(@Qualifier("likeRedisTemplate") RedisTemplate<Long, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public ProductLikeResponse addLike(User user, Long productId) {
        SetOperations<Long, Long> setOperations = redisTemplate.opsForSet();
        setOperations.add(productId, user.getId());
        return new ProductLikeResponse(countProductLikesByProductId(productId));
    }

    @Transactional
    public ProductLikeResponse deleteLike(User user, Long productId) {
        SetOperations<Long, Long> setOperations = redisTemplate.opsForSet();
        setOperations.remove(productId, user.getId());
        return new ProductLikeResponse(countProductLikesByProductId(productId));
    }

    @Transactional(readOnly = true)
    public ProductLikeResponse getProductLikesByProductId(Long productId) {
        return new ProductLikeResponse(countProductLikesByProductId(productId));
    }

    @Transactional(readOnly = true)
    public List<Long> getUsersByProductId(Long productId) {
        Set<Long> userIds = redisTemplate.opsForSet().members(productId);
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
}
