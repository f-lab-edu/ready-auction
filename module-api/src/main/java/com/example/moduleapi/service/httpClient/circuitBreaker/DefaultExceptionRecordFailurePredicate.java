package com.example.moduleapi.service.httpClient.circuitBreaker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;

import java.util.function.Predicate;

@Slf4j
public class DefaultExceptionRecordFailurePredicate implements Predicate<Throwable> {
    /*
     * 어떤 예외를 Fail로 기록할 것인지를 결정.
     * 반환값이 True면 Fail로 기록.
     * 4XX 클라이언트 에러는 fail로 기록하지 않음
     */
    @Override
    public boolean test(Throwable throwable) {
        if (throwable instanceof HttpClientErrorException) {
            return false;
        }
        return true;
    }
}
