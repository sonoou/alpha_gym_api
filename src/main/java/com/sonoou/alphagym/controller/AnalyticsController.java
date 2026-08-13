package com.sonoou.alphagym.controller;

import com.sonoou.alphagym.dto.AnalyticsSummaryResponse;
import com.sonoou.alphagym.dto.DailyAnalyticsRequest;
import com.sonoou.alphagym.dto.WaterIntakeRequest;
import com.sonoou.alphagym.dto.WaterIntakeResponse;
import com.sonoou.alphagym.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryResponse> getSummary(Authentication authentication) {
        AnalyticsSummaryResponse summary = analyticsService.getSummary(authentication.getName());
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/daily")
    public ResponseEntity<Void> logDailyAnalytics(Authentication authentication,
                                                   @RequestBody DailyAnalyticsRequest request) {
        analyticsService.saveDaily(authentication.getName(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/water")
    public ResponseEntity<WaterIntakeResponse> getWaterIntake(Authentication authentication) {
        WaterIntakeResponse response = analyticsService.getWaterIntake(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/water")
    public ResponseEntity<WaterIntakeResponse> updateWaterIntake(Authentication authentication,
                                                                   @RequestBody WaterIntakeRequest request) {
        WaterIntakeResponse response = analyticsService.updateWaterIntake(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }
}
