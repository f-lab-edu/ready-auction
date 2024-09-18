package com.example.readyauction.repository.auction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.readyauction.domain.user.User;

@Repository
public class EmitterRepository {
    // 동시성 문제로 예방을 위해 ConcurrentHashMap 사용
    // 1. 경매 최고가 비교 race cnodition 클러스터링 환경이라는 것을 가정하고 고민
    private final Map<Long, List<Map<User, SseEmitter>>> emittersMap = new ConcurrentHashMap<>();

    public void save(Long productId, User user, SseEmitter emitter) {
        List<Map<User, SseEmitter>> emitters = emittersMap.computeIfAbsent(productId, k -> new ArrayList<>());
        Map<User, SseEmitter> emitterMap = new HashMap<>();
        emitterMap.put(user, emitter);
        emitters.add(emitterMap);
    }

    public void deleteEmitter(User user, Long productId) {
        List<Map<User, SseEmitter>> emitters = this.get(productId);
        for (Map<User, SseEmitter> emitterMap : emitters) {
            if (emitterMap.containsKey(user)) {
                emitterMap.remove(user);
                break;
            }
        }
    }

    public void deleteAll(Long productId) {
        emittersMap.remove(productId);
    }

    public List<Map<User, SseEmitter>> get(Long productId) {
        return emittersMap.get(productId);
    }
}
