package com.example.moduledomain.domain.point;

import com.example.moduledomain.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "points")
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "point", nullable = false)
    private Long amount;

    public void plus(Long plusAmount) {
        this.amount += plusAmount;
    }

    public void minus(Long minusAmount) {
        this.amount -= minusAmount;
    }

    @Builder
    public Point(Long userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
