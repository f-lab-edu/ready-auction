package com.example.moduleworker.Service;

import com.example.moduledomain.repository.kafka.KafkaEmitterRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class KafkaEmitterService {
    private final KafkaEmitterRepository kafkaEmitterRepository;

    public KafkaEmitterService(KafkaEmitterRepository kafkaEmitterRepository) {
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

        sendToClient(emitter, emitterId, "SSE connection established. Preventing 503 error."); // 503 에러방지 데이터

        return emitter;
    }

    public SseEmitter getAdminEmitter() {
        return kafkaEmitterRepository.getEmitter("admin");
    }

    public void sendToClient(SseEmitter emitter, String emitterId, Object data) {

        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("경매 상품 최고가 변경")
                    .data(data));
        } catch (IOException e) {
            kafkaEmitterRepository.deleteById(emitterId);
            emitter.completeWithError(e); // 이게 필수인지는 모르겠음
        }
    }
}
