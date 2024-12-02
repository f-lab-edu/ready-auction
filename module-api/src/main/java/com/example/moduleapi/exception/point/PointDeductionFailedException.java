package com.example.moduleapi.exception.point;

public class PointDeductionFailedException extends RuntimeException {
    public PointDeductionFailedException(Long userId) {
        super(userId + ": 포인트가 부족합니다.");
    }
}
