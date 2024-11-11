package com.example.moduleworker.controller;

import com.example.moduleworker.service.EmitterService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class KafkaNotificationController {
    private final EmitterService emitterService;

    public KafkaNotificationController(EmitterService emitterService) {
        this.emitterService = emitterService;
    }

    @GetMapping(value = "/auction/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeKafkaEvent() {
        return emitterService.addEmitter("admin");
    }
}
