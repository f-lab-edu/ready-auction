package com.example.moduleapi.service.auction;

import com.example.moduleapi.service.product.ProductFacade;
import com.example.moduledomain.common.response.ProductFindResponse;
import com.example.moduledomain.domain.user.CustomUserDetails;
import com.example.moduledomain.domain.user.User;
import com.example.moduledomain.repository.auction.EmitterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Service
public class HighestBidSseNotificationService {
    private final EmitterRepository emitterRepository;
    private final ProductFacade productFacade;
    private static final String SSE_INIT_MESSAGE = "SSE CONNECT";

    public HighestBidSseNotificationService(EmitterRepository emitterRepository, ProductFacade productFacade) {
        this.emitterRepository = emitterRepository;
        this.productFacade = productFacade;
    }

    // SSE 구독
    @Transactional
    public SseEmitter subscribe(CustomUserDetails user, Long productId) {
        ProductFindResponse product = productFacade.findById(productId);
        LocalDateTime closeDate = product.getCloseDate();
        SseEmitter emitter = createEmitter(user.getUser(), productId, closeDate);

        // 503 에러 방지를 위한 데이터 전송
        // Emitter를 생성하고 나서 만료 시간까지 아무런 데이터도 보내지 않으면 재연결 요청시 503 Service Unavailable 에러가 발생할 수 있음.
        send(emitter, user.getUser(), productId, "SSE Emitter Created. [userId=" + user.getUser().getUserId() + "]", SSE_INIT_MESSAGE);
        return emitter;
    }

    // SSE 구독 해지
    @Transactional
    public void subscribeCancel(CustomUserDetails user, Long productId) {
        // 특정 USER의 Emitter를 제거
        emitterRepository.deleteEmitter(user.getUser(), productId);
    }

    // 해당 경매에 참여한 User들에게 해당 경매 상품의 최고가가 갱신될때마다 SSE 알림.
    public void sendToAllUsers(Long productId, Object data, String comment) {
        Map<User, SseEmitter> sseEmitterMap = emitterRepository.get(productId);
        for (Map.Entry<User, SseEmitter> entry : sseEmitterMap.entrySet()) {
            User user = entry.getKey();
            SseEmitter sseEmitter = entry.getValue();
            send(sseEmitter, user, productId, data, comment);
        }
    }

    private void send(SseEmitter sseEmitter, User user, Long productId, Object data, String comment) {
        try {
            sseEmitter.send(SseEmitter.event() // SSE 이벤트를 생성하고 해당 Emitter로 전송합.
                                      .id(productId.toString()) // 이벤트의 고유 ID (문자열 형태로 변환)
                                      .name("THE HIGHEST PRICE UPDATE") // 이벤트의 이름을 "THE HIGHEST PRICE UPDATE"로 지정
                                      .data(data) // 전송할 데이터
                                      .comment(comment)); // 이벤트에 대한 코멘트
        } catch (IOException e) {
            emitterRepository.deleteEmitter(user, productId);
            sseEmitter.completeWithError(e);
        }
    }

    private SseEmitter createEmitter(User user, Long productId, LocalDateTime closeDate) {
        // 경매가 끝나기 전까지 계속 최고가 알람을 줘야해서 경매 종료일 - 요청 시간 = SSE TimeOut
        Long requestTime = convertTimeStamp(LocalDateTime.now());
        Long timeOut = convertTimeStamp(closeDate) - requestTime;

        SseEmitter emitter = new SseEmitter(timeOut);
        emitterRepository.save(productId, user, emitter);
        emitter.onCompletion(() -> emitterRepository.deleteEmitter(user, productId));
        emitter.onTimeout(() -> emitterRepository.deleteEmitter(user, productId));

        return emitter;
    }

    private Long convertTimeStamp(LocalDateTime time) {
        return time.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
    }
}
