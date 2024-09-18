package com.example.readyauction.repository.auction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.readyauction.domain.user.User;

@Repository
public class EmitterRepository {
    // 동시성 문제로 예방을 위해 ConcurrentHashMap 사용
    // 1. 경매 최고가 비교 race cnodition 클러스터링 환경이라는 것을 가정하고 고민
    private final Map<Long, Map<User, SseEmitter>> emittersMaps = new ConcurrentHashMap<>();

    public void save(Long productId, User user, SseEmitter emitter) {
        Map<User, SseEmitter> emitters = emittersMaps.computeIfAbsent(productId, k -> new HashMap<>());
        emitters.put(user, emitter);
    }

    public void deleteEmitter(User user, Long productId) {
        Map<User, SseEmitter> emitters = this.get(productId);
        emitters.remove(user);
    }

    public void deleteAll(Long productId) {
        emittersMaps.remove(productId);
    }

    public Map<User, SseEmitter> get(Long productId) {
        return emittersMaps.get(productId);
    }
}
