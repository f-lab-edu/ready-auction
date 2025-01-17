package com.example.moduleapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.like.host}")
    private String likeRedisHost;

    @Value("${spring.data.redis.like.port}")
    private int likeRedisPort;

    @Value("${spring.data.redis.session.host}")
    private String sessionRedisHost;

    @Value("${spring.data.redis.session.port}")
    private int sessionRedisPort;

    @Bean
    public RedisConnectionFactory likeRedisConnectionFactory() {
        return new LettuceConnectionFactory(likeRedisHost, likeRedisPort);
    }

    @Bean
    public RedisTemplate<Long, Long> likeRedisTemplate() {
        RedisTemplate<Long, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(likeRedisConnectionFactory());

        // redis-cli을 통해 직접 데이터 조회 시 알아볼 수 없는 형태로 출력되는 것을 방지
        redisTemplate.setKeySerializer(new GenericToStringSerializer<>(Long.class));
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        return redisTemplate;
    }

    @Bean
    public RedisConnectionFactory sessionRedisConnectionFactory() {
        return new LettuceConnectionFactory(sessionRedisHost, sessionRedisPort);
    }

    @Bean
    public RedisTemplate<Long, Long> sessionRedisTemplate() {
        RedisTemplate<Long, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(likeRedisConnectionFactory());

        // redis-cli을 통해 직접 데이터 조회 시 알아볼 수 없는 형태로 출력되는 것을 방지
        redisTemplate.setKeySerializer(new GenericToStringSerializer<>(Long.class));
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        return redisTemplate;
    }
}
