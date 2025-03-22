package com.example.moduleapi.controller.response.point;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointResponse {
    private Long id;
    private Long currentPoint;
    private String message;

    @Builder
    public PointResponse(Long id, Long currentPoint, String message) {
        this.id = id;
        this.currentPoint = currentPoint;
        this.message = message;
    }


    public static PointResponse from(Long id, Long currentPoint, String message) {
        return PointResponse.builder()
                            .id(id)
                            .currentPoint(currentPoint)
                            .message(message)
                            .build();
    }
}
