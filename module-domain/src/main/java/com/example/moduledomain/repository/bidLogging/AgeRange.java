package com.example.moduledomain.repository.bidLogging;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AgeRange {
    private int minAge;
    private int maxAge;

    public AgeRange(int minAge, int maxAge) {
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public Integer getIntegerAge() {
        return (this.getMinAge() / 10) * 10;
    }
}

