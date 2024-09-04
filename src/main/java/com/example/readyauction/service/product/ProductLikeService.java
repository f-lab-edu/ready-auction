package com.example.readyauction.service.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.readyauction.domain.user.User;

@Service
public class ProductLikeService {

    private final RedisTemplate<Long, Long> redisTemplate;

    public ProductLikeService(RedisTemplate<Long, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public int addLike(User user, Long productId) {
        SetOperations<Long, Long> setOperations = redisTemplate.opsForSet();
        setOperations.add(productId, user.getId());
        return countProductLikesByProductId(productId);
    }

    @Transactional
    public int deleteLike(User user, Long productId) {
        SetOperations<Long, Long> setOperations = redisTemplate.opsForSet();
        setOperations.remove(productId, user.getId());
        return countProductLikesByProductId(productId);
    }

    @Transactional
    public int getProductLikesByProductId(Long productId) {
        return countProductLikesByProductId(productId);
    }

    @Transactional
    public List<Long> getUsersByProductId(Long productId) {
        Set<Long> userIds = redisTemplate.opsForSet().members(productId);
        return new ArrayList<>(userIds);
    }

    @Transactional
    public int countProductLikesByProductId(Long productId) {
        Long count = redisTemplate.opsForSet().size(productId);
        return count == null ? 0 : count.intValue();
    }
}
