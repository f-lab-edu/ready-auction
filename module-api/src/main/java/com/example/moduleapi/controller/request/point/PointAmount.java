package com.example.moduleapi.controller.request.point;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointAmount {
    private int amount;

    public PointAmount(int amount) {
        this.amount = amount;
    }
}
