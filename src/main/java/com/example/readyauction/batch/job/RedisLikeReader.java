package com.example.readyauction.batch.job;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import com.example.readyauction.domain.product.ProductLike;
import com.example.readyauction.service.product.ProductLikeService;

@Component
public class RedisLikeReader implements ItemReader<List<ProductLike>> {
    @Value("${ready.auction.spring.batch.chunkSize}")
    public int CHUNK_SIZE;

    private final RedisTemplate<Long, Long> redisTemplate;
    private final ProductLikeService productLikeService;

    public RedisLikeReader(RedisTemplate<Long, Long> redisTemplate, ProductLikeService productLikeService) {
        this.redisTemplate = redisTemplate;
        this.productLikeService = productLikeService;
    }

    @Override
    public List<ProductLike> read() throws
        Exception,
        UnexpectedInputException,
        ParseException,
        NonTransientResourceException {

        List<ProductLike> productLikes = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(CHUNK_SIZE).build();
        Cursor<Long> cursor = redisTemplate.scan(scanOptions);

        while (cursor.hasNext()) {
            Long productId = cursor.next();
            productLikeService.getUsersByProductId(productId)
                .stream()
                .map(userId -> new ProductLike(productId, userId))
                .forEach(productLikes::add);

        }
        return productLikes;
    }
}
