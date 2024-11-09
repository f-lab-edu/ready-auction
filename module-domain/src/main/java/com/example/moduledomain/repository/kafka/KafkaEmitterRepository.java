package com.example.moduledomain.repository.kafka;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class KafkaEmitterRepository {
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public void save(String emitterId, SseEmitter emitter) {
        emitterMap.put(emitterId, emitter);
    }

    public SseEmitter getEmitter(String emitterId) {
        return emitterMap.get(emitterId);
    }

    public void deleteById(String emitterId) {
        emitterMap.remove(emitterId);
    }
}
