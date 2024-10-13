package com.example.readyauction.controller.auction;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.readyauction.controller.request.auction.BidRequest;
import com.example.readyauction.controller.response.auction.BidResponse;
import com.example.readyauction.domain.user.CustomUserDetails;
import com.example.readyauction.service.auction.AuctionService;

@RestController
@RequestMapping("/api/v1/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    // 경매 참여 API - SSE 구독 API
    @GetMapping(value = "/product/{productId}/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long productId) {
        return auctionService.subscribe(user, productId);
    }

    // 경매 참여 취소 API - SSE 구독 해지 API
    @GetMapping(value = "/{productId}/subscribe/cancel")
    public void subscribeCancel(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long productId) {
        auctionService.subscribeCancel(user, productId);
    }

    // 가격 입찰 API
    @PostMapping(value = "/{productId}")
    public BidResponse tender(@AuthenticationPrincipal CustomUserDetails user,
        @RequestBody BidRequest bidRequest,
        @PathVariable Long productId) {
        return auctionService.biddingPrice(user, bidRequest, productId);
    }
}
