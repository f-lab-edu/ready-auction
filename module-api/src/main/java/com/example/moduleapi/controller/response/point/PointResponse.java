package com.example.moduleapi.controller.response.point;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointResponse {
    private Long currentPoint;
    private String message;

    @Builder
    public PointResponse(Long currentPoint, String message) {
        this.currentPoint = currentPoint;
        this.message = message;
    }

    public static PointResponse from(Long currentPoint, String message) {
        return PointResponse.builder()
                            .currentPoint(currentPoint)
                            .message(message)
                            .build();
    }
}
