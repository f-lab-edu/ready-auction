package com.example.readyauction.service.product;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.readyauction.domain.user.User;

@Service
public class ProductLikeService {
	/*
		1. Redis에 해당 사용자(id)가 해당 상품(id)에 대한 "좋아요" 정보가 없으면 좋아요 + 1
		2. Redis에 해당 사용자(id)가 해당 상품(id)에 대한 "좋아요" 정보가 있으면 좋아요 - 1
		3. Redis에 좋아요 정보가 있는지 없는지를 확인하는 메서드
	 */

	private final RedisTemplate<Long, Long> redisTemplate;

	public ProductLikeService(RedisTemplate<Long, Long> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Transactional
	public int productLike(User user, Long productId) {
		if (checkIfUserLikesProduct(user, productId)) {
			return deleteProductLike(user, productId);
		} else {
			return addProductLike(user, productId);
		}
	}

	private int addProductLike(User user, Long productId) {
		SetOperations<Long, Long> setOperations = redisTemplate.opsForSet();
		setOperations.add(productId, user.getId());
		return countProductLikesByProductId(productId);
	}

	private int deleteProductLike(User user, Long productId) {
		SetOperations<Long, Long> setOperations = redisTemplate.opsForSet();
		setOperations.remove(productId, user.getId());
		return countProductLikesByProductId(productId);
	}

	@Transactional
	public int getProductLikesByProductId(Long productId) {
		return countProductLikesByProductId(productId);
	}

	@Transactional
	public Set<Long> getUsersByProductId(Long productId) {
		Set<Long> userIds = redisTemplate.opsForSet().members(productId);
		return userIds;
	}

	@Transactional
	public int countProductLikesByProductId(Long productId) {
		Long count = redisTemplate.opsForSet().size(productId);
		return count == null ? 0 : count.intValue();
	}

	private Boolean checkIfUserLikesProduct(User user, Long productId) {
		if (user == null) {
			return Boolean.FALSE;
		}
		Set<Long> userIds = redisTemplate.opsForSet().members(productId);
		if (userIds == null) {
			return Boolean.FALSE;
		}
		return userIds.contains(user.getId());
	}
}
