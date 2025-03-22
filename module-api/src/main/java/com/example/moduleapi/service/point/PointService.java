package com.example.moduleapi.service.point;

import com.example.moduleapi.controller.request.point.PointAmount;
import com.example.moduleapi.controller.response.point.PointResponse;
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

    @Transactional
    public PointResponse chargePoint(CustomUserDetails userDetails, PointAmount pointAmount) {
        User user = userDetails.getUser();
        Point point = pointRepository.findByUserId(user.getId());
        point.plus(pointAmount.getAmount());
        Long currentPoint = point.getAmount();
        return PointResponse.from(point.getId(), currentPoint, String.format("포인트 %d원 충전 완료. [현재 포인트 잔액 : %d원]", pointAmount.getAmount(), currentPoint));
    }

    @Transactional
    public Long deductPoint(CustomUserDetails userDetails, PointAmount pointAmount) {
        User user = userDetails.getUser();
        Point point = pointRepository.findByUserId(user.getId());

        validateDeductPoint(pointAmount, user, point);

        point.minus(pointAmount.getAmount());
        return point.getAmount();
    }

    @Transactional
    public void rollbackPoint(Long userId, Long plusAmount) {
        Point point = pointRepository.findByUserId(userId);
        point.plus(plusAmount);
    }

    private static void validateDeductPoint(PointAmount pointAmount, User user, Point point) {
        if (point.getAmount() < pointAmount.getAmount()) {
            throw new PointDeductionFailedException(user.getId());
        }
    }
}
