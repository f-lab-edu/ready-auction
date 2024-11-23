package com.example.moduleapi.fixture.point

import com.example.moduledomain.domain.point.Point

class PointFixtures {
    static Point createPoint(Map map = [:]) {
        return Point.builder()
                .userId(map.getOrDefault("userId", 1L) as Long)
                .amount(map.getOrDefault("amount", 0) as int)
                .build()
    }

}

