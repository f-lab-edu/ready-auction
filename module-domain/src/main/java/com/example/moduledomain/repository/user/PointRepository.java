package com.example.moduledomain.repository.user;

import com.example.moduledomain.domain.point.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
    Point findByUserId(Long userId);
}
