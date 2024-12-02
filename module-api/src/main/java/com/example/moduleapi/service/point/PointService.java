package com.example.moduleapi.service.point;

import com.example.moduleapi.controller.request.point.PointAmount;
import com.example.moduleapi.exception.point.PointDeductionFailedException;
import com.example.moduledomain.domain.point.Point;
import com.example.moduledomain.domain.user.CustomUserDetails;
import com.example.moduledomain.domain.user.User;
import com.example.moduledomain.repository.user.PointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointService {
    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    // 포인트 충전
    @Transactional
    public int chargePoint(CustomUserDetails userDetails, PointAmount pointAmount) {
        User user = userDetails.getUser();
        Point point = pointRepository.findByUserId(user.getId());
        point.plus(pointAmount.getAmount());
        return point.getAmount();
    }

    // 포인트 차감
    @Transactional
    public int deductPoint(CustomUserDetails userDetails, PointAmount pointAmount) {
        User user = userDetails.getUser();
        Point point = pointRepository.findByUserId(user.getId());

        validateDeductPoint(pointAmount, user, point);

        point.minus(pointAmount.getAmount());
        return point.getAmount();
    }

    // 포인트 롤백
    @Transactional
    public void rollbackPoint(Long userId, int plusAmount) {
        Point point = pointRepository.findByUserId(userId);
        point.plus(plusAmount);
    }

    private static void validateDeductPoint(PointAmount pointAmount, User user, Point point) {
        if (point.getAmount() < pointAmount.getAmount()) {
            throw new PointDeductionFailedException(user.getId());
        }
    }
}
