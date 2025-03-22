package com.example.moduleapi.service.product

import com.example.moduleapi.controller.response.product.ProductLikeResponse
import com.example.moduleapi.fixture.product.ProductFixtures
import com.example.moduleapi.fixture.user.UserFixtures
import com.example.moduledomain.domain.product.Product
import com.example.moduledomain.domain.user.User
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.ZSetOperations
import spock.lang.Specification

class ProductLikeServiceTest extends Specification {
    RedisTemplate<String, Long> redisTemplate = Mock()
    ValueOperations<String, Long> valueOperations = Mock()
    ZSetOperations<String, Long> zsetOperations = Mock()
    ProductLikeService productLikeService = new ProductLikeService(redisTemplate)

    def "상품에 대한 좋아요를 추가한다."() {
        given:
        redisTemplate.opsForZSet() >> zsetOperations
        redisTemplate.opsForValue() >> valueOperations

        User user = UserFixtures.createUser()
        String userKey = "user:" + user.getId()

        Product product = ProductFixtures.createProduct()
        String productKey = "product:" + product.getId()

        Long likeCount = 10L
        valueOperations.get(productKey) >> likeCount
        zsetOperations.score(userKey, product.getId()) >> null

        when:
        ProductLikeResponse response = productLikeService.addLike(user, product.getId())

        then:
        response.likesCount == likeCount + 1
    }

    def "상품에 대한 좋아요를 삭제한다"() {
        given:
        redisTemplate.opsForZSet() >> zsetOperations
        redisTemplate.opsForValue() >> valueOperations

        User user = UserFixtures.createUser()
        String userKey = "user:" + user.getId()

        Product product = ProductFixtures.createProduct()
        String productKey = "product:" + product.getId()

        Long likeCount = 10L
        valueOperations.get(productKey) >> likeCount

        Double score = 1.5
        zsetOperations.score(userKey, product.getId()) >> score

        when:
        ProductLikeResponse response = productLikeService.deleteLike(user, product.getId())

        then:
        response.likesCount == likeCount - 1
    }

    def "상품의 좋아요 개수를 조회한다"() {
        given:
        redisTemplate.opsForZSet() >> zsetOperations
        redisTemplate.opsForValue() >> valueOperations

        Product product = ProductFixtures.createProduct()
        String productKey = "product:" + product.getId()

        Long likeCount = 10L
        valueOperations.get(productKey) >> likeCount

        when:
        def response = productLikeService.countProductLikesByProductId(product.getId())

        then:
        response == likeCount
    }
}
