package com.example.readyauction.batch;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.readyauction.domain.product.ProductLike;
import com.example.readyauction.repository.ProductLikeRepository;

@Component
public class RedisLikeWriter implements ItemWriter<ProductLike> {
	private final ProductLikeRepository productLikeRepository;

	public RedisLikeWriter(ProductLikeRepository productLikeRepository) {
		this.productLikeRepository = productLikeRepository;
	}

	@Override
	public void write(Chunk<? extends ProductLike> chunk) throws Exception {
		List<? extends ProductLike> items = chunk.getItems();
		productLikeRepository.saveAll(items);

	}
}
