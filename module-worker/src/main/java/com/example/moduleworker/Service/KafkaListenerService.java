package com.example.moduleworker.Service;

import com.example.moduleapi.service.dto.AuctionPriceChangeNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class KafkaListenerService {

    private final KafkaEmitterService kafkaEmitterService;

    public KafkaListenerService(KafkaEmitterService kafkaEmitterService) {
        this.kafkaEmitterService = kafkaEmitterService;
    }

    @KafkaListener(topics = "Auction-Price-Change-Notification", groupId = "auction-group")
    public void listen(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AuctionPriceChangeNotification notification = objectMapper.readValue(message, AuctionPriceChangeNotification.class);
            SseEmitter adminEmitter = kafkaEmitterService.getAdminEmitter();
            kafkaEmitterService.sendToClient(adminEmitter, "admin", notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
