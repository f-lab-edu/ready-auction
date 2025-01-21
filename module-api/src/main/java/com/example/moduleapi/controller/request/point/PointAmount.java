package com.example.moduleapi.controller.request.point;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointAmount {
    private int amount;

    @Builder
    public PointAmount(int amount) {
        this.amount = amount;
    }
}
