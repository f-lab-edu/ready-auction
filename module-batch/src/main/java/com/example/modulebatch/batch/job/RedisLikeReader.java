package com.example.modulebatch.batch.job;

import com.example.moduleapi.service.product.ProductLikeService;
import com.example.modulebatch.batch.config.LikeBatchConfig;
import com.example.moduledomain.domain.product.ProductLike;
import com.example.moduledomain.repository.product.ProductLikeRepository;
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

@Component("RedisLikeReader")
public class RedisLikeReader implements ItemReader<List<ProductLike>> {
    public int CHUNK_SIZE = LikeBatchConfig.CHUNK_SIZE;

    private final RedisTemplate<Long, Long> redisTemplate;
    private final ProductLikeService productLikeService;
    private final ProductLikeRepository productLikeRepository;
    private boolean isEndOfData = false;

    public RedisLikeReader(RedisTemplate<Long, Long> redisTemplate,
                           ProductLikeService productLikeService,
                           ProductLikeRepository productLikeRepository) {
        this.redisTemplate = redisTemplate;
        this.productLikeService = productLikeService;
        this.productLikeRepository = productLikeRepository;
    }

    @Override
    public List<ProductLike> read() throws
            Exception,
            UnexpectedInputException,
            ParseException,
            NonTransientResourceException {

        if (isEndOfData) {
            return null;
        }

        List<ProductLike> productLikes = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(CHUNK_SIZE).build();
        Cursor<Long> cursor = redisTemplate.scan(scanOptions);

        while (cursor.hasNext()) {
            Long productId = cursor.next();
            List<Long> userIds = productLikeService.getUsersByProductId(productId);

            for (Long userId : userIds) {
                boolean alreadyExist = productLikeRepository.existsByProductIdAndUserId(productId, userId);
                if (!alreadyExist) {
                    ProductLike productLike = new ProductLike(productId, userId);
                    productLikes.add(productLike);
                }
            }

        }

        if (cursor.getId().getCursorId().equals("0")) {
            isEndOfData = true;
        }

        return productLikes;
    }
}
