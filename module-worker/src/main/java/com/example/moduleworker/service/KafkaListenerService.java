package com.example.moduleworker.service;

import com.example.moduleapi.service.dto.AuctionPriceChangeNotification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class KafkaListenerService {

    private final EmitterService emitterService;

    public KafkaListenerService(EmitterService emitterService) {
        this.emitterService = emitterService;
    }

    @KafkaListener(topics = "Auction-Price-Change-Notification", groupId = "auction-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(AuctionPriceChangeNotification notification) {
        SseEmitter adminEmitter = emitterService.getAdminEmitter();
        emitterService.sendToClient(adminEmitter, "admin", "경매 상품 최고가 변경", notification);
    }
}
