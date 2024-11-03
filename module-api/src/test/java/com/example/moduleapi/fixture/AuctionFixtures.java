package com.example.moduleapi.fixture;

import com.example.moduleapi.controller.request.auction.BidRequest;
import com.example.moduleapi.controller.response.auction.BidResponse;
import com.example.moduleapi.exception.auction.BiddingFailException;
import com.example.moduleapi.exception.auction.RedisLockAcquisitionException;
import com.example.moduleapi.exception.auction.RedisLockInterruptedException;

public class AuctionFixtures {
    public static Long 상품_아이디 = 1L;
    public static double 최고가_상승률 = 4.5;
    public static BidRequest 가격_제안_요청 = new BidRequest(100000);
    public static BidResponse 가격_제안_응답 = BidResponse.from(1L, 4.5);

    public static BiddingFailException 가격_제안_실패_예외 = new BiddingFailException("testUser", 50000, 1L);
    public static RedisLockAcquisitionException 레디스_분산락_획득실패_예외 = new RedisLockAcquisitionException(1L);
    public static RedisLockInterruptedException 레디스_분산락_처리과정_예외 = new RedisLockInterruptedException(1L,
            new RuntimeException("Redis 분산락 처리 과정에서 발생한 예외 메시지"));

}
