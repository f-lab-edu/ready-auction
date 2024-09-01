package com.example.readyauction.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import com.example.readyauction.domain.product.ProductLike;
import com.example.readyauction.service.product.ProductLikeService;

@Component
public class RedisLikeReader implements ItemReader<List<ProductLike>> {
	private final RedisTemplate<Long, Long> redisTemplate;
	private final ProductLikeService productLikeService;

	public RedisLikeReader(RedisTemplate<Long, Long> redisTemplate, ProductLikeService productLikeService) {
		this.redisTemplate = redisTemplate;
		this.productLikeService = productLikeService;
	}

	private List<ProductLike> productLikes = new ArrayList<>();

	@Override
	public List<ProductLike> read() throws
		Exception,
		UnexpectedInputException,
		ParseException,
		NonTransientResourceException {
		ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(10).build();
		Cursor<Long> cursor = redisTemplate.scan(scanOptions);

		while (cursor.hasNext()) {
			Long productId = cursor.next();
			Set<Long> usersIds = productLikeService.getUsersByProductId(productId);
			if (usersIds != null) {
				for (Long userId : usersIds) {
					ProductLike productLike = new ProductLike(productId, userId);
					productLikes.add(productLike);
				}
			}
		}
		return productLikes;
	}
}
