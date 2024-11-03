package com.example.modulebatch.batch.job;

import com.example.moduleapi.service.product.ProductLikeService;
import com.example.modulebatch.batch.config.BatchConfig;
import com.example.moduledomain.domain.product.ProductLike;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("LikeRedisReader")
public class RedisLikeReader implements ItemReader<List<ProductLike>> {
    public int CHUNK_SIZE = BatchConfig.CHUNK_SIZE;

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
