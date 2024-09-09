package com.example.readyauction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
public class RedisConfig {
	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	public RedisTemplate<Long, Long> redisTemplate() {
		RedisTemplate<Long, Long> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());

		// redis-cli을 통해 직접 데이터 조회 시 알아볼 수 없는 형태로 출력되는 것을 방지
		redisTemplate.setKeySerializer(new GenericToStringSerializer<>(Long.class));
		redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
		return redisTemplate;
	}
}
