package com.example.modulebatch.batch.job;

import com.example.moduleapi.service.product.ProductLikeService;
import com.example.modulebatch.batch.config.LikeBatchConfig;
import com.example.moduledomain.domain.product.ProductLike;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("RedisLikeReader")
public class RedisLikeReader implements ItemReader<List<ProductLike>> {
    public int CHUNK_SIZE = LikeBatchConfig.CHUNK_SIZE;

    private final RedisTemplate<String, Long> redisTemplate;
    private final ProductLikeService productLikeService;
    private boolean isEndOfData = false;

    public RedisLikeReader(RedisTemplate<String, Long> redisTemplate, ProductLikeService productLikeService) {
        this.redisTemplate = redisTemplate;
        this.productLikeService = productLikeService;
    }

    @Override
    public List<ProductLike> read() {
        if (isEndOfData) {
            return null;
        }

        List<ProductLike> productLikes = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match("product:*").count(CHUNK_SIZE).build();
        Cursor<String> cursor = redisTemplate.scan(scanOptions);

        while (cursor.hasNext()) {
            String productkey = cursor.next();
            Long productId = extractProductId(productkey);
            int productLikeCount = productLikeService.countProductLikesByProductId(productId);
            productLikes.add(new ProductLike(productId, productLikeCount));
        }

        if (cursor.getId().getCursorId().equals("0")) {
            isEndOfData = true;
        }

        return productLikes;
    }

    private Long extractProductId(String productKey) {
        return Long.parseLong(productKey.split(":")[1]);
    }
}
