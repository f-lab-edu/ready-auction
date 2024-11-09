package com.example.moduleworker.controller;

import com.example.moduleworker.Service.KafkaEmitterService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class KafkaNotificationController {
    private final KafkaEmitterService kafkaEmitterService;

    public KafkaNotificationController(KafkaEmitterService kafkaEmitterService) {
        this.kafkaEmitterService = kafkaEmitterService;
    }

    @GetMapping(value = "/auction/kafkaEvent/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeKafkaEvent() {
        return kafkaEmitterService.addEmitter("admin");
    }
}
