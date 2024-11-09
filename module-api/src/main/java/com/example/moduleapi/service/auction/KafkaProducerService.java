package com.example.moduleapi.service.auction;

import com.example.moduleapi.service.dto.AuctionPriceChangeNotification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, com.example.moduleapi.service.dto.AuctionPriceChangeNotification> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, AuctionPriceChangeNotification> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void publishAuctionPriceChangeNotification(Long productId, Long newPrice) {
        AuctionPriceChangeNotification notification = AuctionPriceChangeNotification.builder()
                .productId(productId)
                .newPrice(newPrice)
                .build();

        kafkaTemplate.send("Auction-Price-Change-Notification", notification);

    }
}
