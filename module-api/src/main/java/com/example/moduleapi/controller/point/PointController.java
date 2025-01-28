package com.example.moduleapi.controller.point;

import com.example.moduleapi.controller.request.point.PointAmount;
import com.example.moduleapi.controller.response.point.PointResponse;
import com.example.moduleapi.service.point.PointService;
import com.example.moduledomain.domain.user.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/point")
public class PointController {
    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @PostMapping("/charge")
    public PointResponse chargePoint(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     @RequestBody PointAmount pointAmount) {
        return pointService.chargePoint(customUserDetails, pointAmount);
    }
}
