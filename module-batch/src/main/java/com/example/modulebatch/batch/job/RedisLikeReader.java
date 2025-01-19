package com.example.modulebatch.batch.job;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import com.example.moduleapi.service.product.ProductLikeService;
import com.example.modulebatch.batch.config.LikeBatchConfig;
import com.example.moduledomain.domain.product.ProductLike;

@Component("RedisLikeReader")
public class RedisLikeReader implements ItemReader<List<ProductLike>> {
    public int CHUNK_SIZE = LikeBatchConfig.CHUNK_SIZE;

    private final RedisTemplate<Long, Long> redisTemplate;
    private final ProductLikeService productLikeService;
    private boolean isEndOfData = false;  // 상태 변수 추가

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

        // 데이터가 끝났다면 null을 반환하여 Step 종료
        if (isEndOfData) {
            return null;  // 마지막 호출에서 null을 반환하여 Step 종료를 알림
        }

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

        // 커서가 끝에 도달한 경우, 상태를 변경하고 null을 반환할 준비
        if (cursor.getId().getCursorId().equals("0")) {
            isEndOfData = true;  // 마지막 데이터 처리 후 종료 상태로 변경
        }

        return productLikes;  // 현재 읽은 데이터를 반환
    }
}
