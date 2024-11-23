package com.example.moduledomain.domain.user;

import com.example.moduledomain.domain.BaseEntity;
import jakarta.persistence.*;
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

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "point")
    private int point;

    public void plus(int plusPoint) {
        this.point += plusPoint;
    }

    public void minus(int minusPoint) {
        this.point -= minusPoint;
    }
}
