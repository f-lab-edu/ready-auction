package com.example.modulebatch.batch.job;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.moduledomain.domain.product.ProductLike;
import com.example.moduledomain.repository.product.ProductLikeRepository;

@Component("RedisLikeWriter")
public class RedisLikeWriter implements ItemWriter<List<ProductLike>> {
    private final ProductLikeRepository productLikeRepository;

    public RedisLikeWriter(ProductLikeRepository productLikeRepository) {
        this.productLikeRepository = productLikeRepository;
    }

    @Override
    public void write(Chunk<? extends List<ProductLike>> chunks) throws Exception {
        for (List<ProductLike> productLikes : chunks) {
            productLikeRepository.saveAll(productLikes);
        }
    }
}
