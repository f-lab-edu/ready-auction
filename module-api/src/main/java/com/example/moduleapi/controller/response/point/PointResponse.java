package com.example.moduleapi.controller.response.point;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointResponse {
    private int currentPoint;
    private String message;

    @Builder
    public PointResponse(int currentPoint, String message) {
        this.currentPoint = currentPoint;
        this.message = message;
    }
}
