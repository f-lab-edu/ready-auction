package com.example.modulebatch.batch.job;

import com.example.moduledomain.domain.product.ProductLike;
import com.example.moduledomain.repository.product.ProductLikeRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("LikeRedisWriter")
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
