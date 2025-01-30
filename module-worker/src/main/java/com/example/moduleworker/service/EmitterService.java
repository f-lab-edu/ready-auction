package com.example.moduleworker.service;

import com.example.moduleworker.repository.KafkaEmitterRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class EmitterService {
    private final KafkaEmitterRepository kafkaEmitterRepository;

    public EmitterService(KafkaEmitterRepository kafkaEmitterRepository) {
        this.kafkaEmitterRepository = kafkaEmitterRepository;
    }

    public SseEmitter addEmitter(String emitterId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        kafkaEmitterRepository.save(emitterId, emitter);

        emitter.onCompletion(() -> {
            kafkaEmitterRepository.deleteById(emitterId);
        });
        emitter.onTimeout(() -> {
            kafkaEmitterRepository.deleteById(emitterId);
        });

        sendToClient(emitter, emitterId, "연결 확인", "SSE connection established. Preventing 503 error."); // 503 에러방지 데이터

        return emitter;
    }

    public SseEmitter getAdminEmitter() {
        return kafkaEmitterRepository.getEmitter("admin");
    }

    public void sendToClient(SseEmitter emitter, String emitterId, String name, Object data) {

        try {
            emitter.send(SseEmitter.event()
                                   .id(emitterId)
                                   .name(name)
                                   .data(data));
        } catch (IOException e) {
            kafkaEmitterRepository.deleteById(emitterId);
            emitter.completeWithError(e);
        }
    }
}
