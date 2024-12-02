package com.example.moduleapi.service.product

import com.example.moduleapi.fixture.user.UserFixtures
import com.example.moduledomain.domain.user.User
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations
import spock.lang.Specification

class ProductLikeServiceTest extends Specification {
    RedisTemplate<Long, Long> redisTemplate = Mock()
    SetOperations setOperations = Mock()
    ProductLikeService productLikeService = new ProductLikeService(redisTemplate)

    def "좋아요"() {
        given:
        User user = UserFixtures.createUser()
        redisTemplate.opsForSet() >> setOperations

        when:
        productLikeService.addLike(user, 1L)

        then:
        1 * setOperations.add(1L, _)
    }

    def "좋아요 삭제"() {
        given:
        User user = UserFixtures.createUser()
        redisTemplate.opsForSet() >> setOperations

        when:
        productLikeService.deleteLike(user, 1L)

        then:
        1 * setOperations.remove(1L, _)
    }

    def "상품 좋아요 개수 조회"() {
        given:
        redisTemplate.opsForSet() >> setOperations
        setOperations.size(1L) >> 10

        when:
        def response = productLikeService.getProductLikesByProductId(1L)

        then:
        response.likesCount == 10
    }

    def "특정 상품 좋아요 누른 유저 조회"() {
        given:
        redisTemplate.opsForSet() >> setOperations
        Set<Long> mockUserIds = [1L, 2L, 3L, 4L, 5L]
        setOperations.members(1L) >> mockUserIds

        when:
        def response = productLikeService.getUsersByProductId(1L)

        then:
        response.size() == 5
    }
}
