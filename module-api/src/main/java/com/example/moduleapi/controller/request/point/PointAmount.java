package com.example.moduleapi.controller.request.point;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointAmount {
    private Long amount;

    @Builder
    public PointAmount(Long amount) {
        this.amount = amount;
    }
}
