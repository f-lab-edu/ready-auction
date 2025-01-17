package com.example.moduleapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class SessionRedisConfig {
    @Value("${spring.data.redis.session.host}")
    private String sessionRedisHost;

    @Value("${spring.data.redis.session.port}")
    private int sessionRedisPort;

    @Bean
    @Primary
    public RedisConnectionFactory sessionRedisConnectionFactory() {
        return new LettuceConnectionFactory(sessionRedisHost, sessionRedisPort);
    }

    @Bean
    public RedisTemplate<String, Object> sessionRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(sessionRedisConnectionFactory());

        // redis-cli을 통해 직접 데이터 조회 시 알아볼 수 없는 형태로 출력되는 것을 방지
        redisTemplate.setKeySerializer(new GenericToStringSerializer<>(String.class));
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return redisTemplate;
    }
}
