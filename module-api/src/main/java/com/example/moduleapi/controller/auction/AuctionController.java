package com.example.moduleapi.controller.auction;

import com.example.moduleapi.controller.request.auction.BidRequest;
import com.example.moduleapi.controller.response.auction.BidResponse;
import com.example.moduleapi.service.auction.AuctionService;
import com.example.moduleapi.service.auction.HighestBidSseNotificationService;
import com.example.moduledomain.domain.user.CustomUserDetails;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final HighestBidSseNotificationService bidSseNotificationService;

    public AuctionController(AuctionService auctionService, HighestBidSseNotificationService bidSseNotificationService) {
        this.auctionService = auctionService;
        this.bidSseNotificationService = bidSseNotificationService;
    }

    // 경매 참여 API - SSE 구독 API
    @GetMapping(value = "/product/{productId}/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails user,
                                @PathVariable Long productId) {
        return bidSseNotificationService.subscribe(user, productId);
    }

    // 경매 참여 취소 API - SSE 구독 해지 API
    @GetMapping(value = "/{productId}/subscribe/cancel")
    public void subscribeCancel(@AuthenticationPrincipal CustomUserDetails user,
                                @PathVariable Long productId) {
        bidSseNotificationService.subscribeCancel(user, productId);
    }

    // 가격 입찰 API
    @PostMapping(value = "/{productId}")
    public BidResponse tender(@AuthenticationPrincipal CustomUserDetails user,
                              @RequestBody BidRequest bidRequest,
                              @PathVariable Long productId) {
        return auctionService.biddingPrice(user, bidRequest, productId);
    }
}
