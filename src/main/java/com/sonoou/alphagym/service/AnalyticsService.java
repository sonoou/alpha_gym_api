package com.sonoou.alphagym.service;

import com.sonoou.alphagym.dto.AnalyticsSummaryResponse;
import com.sonoou.alphagym.dto.DailyAnalyticsRequest;
import com.sonoou.alphagym.dto.WaterIntakeRequest;
import com.sonoou.alphagym.dto.WaterIntakeResponse;
import com.sonoou.alphagym.entity.DailyAnalyticsEntity;
import com.sonoou.alphagym.entity.UserEntity;
import com.sonoou.alphagym.repository.DailyAnalyticsRepository;
import com.sonoou.alphagym.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsService {

    private final DailyAnalyticsRepository analyticsRepository;
    private final UserRepository userRepository;

    public AnalyticsService(DailyAnalyticsRepository analyticsRepository, UserRepository userRepository) {
        this.analyticsRepository = analyticsRepository;
        this.userRepository = userRepository;
    }

    public AnalyticsSummaryResponse getSummary(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now();
        DailyAnalyticsEntity todayAnalytics = analyticsRepository.findByUserIdAndDate(user.getId(), today)
                .orElse(new DailyAnalyticsEntity());

        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        List<DailyAnalyticsEntity> weeklyList = analyticsRepository.findByUserIdAndDateBetween(user.getId(), startOfWeek, today);

        int weeklyActiveMinutes = weeklyList.stream()
                .mapToInt(a -> a.getActiveMinutes() != null ? a.getActiveMinutes() : 0)
                .sum();

        int workoutsThisWeek = user.getTotalWorkoutsCompleted() != null ? user.getTotalWorkoutsCompleted() : 0;
        String primaryCategory = user.getFitnessGoal() != null ? user.getFitnessGoal() : "Strength Training";

        int dailySteps = todayAnalytics.getSteps() != null ? todayAnalytics.getSteps() : 0;
        int dailyCalories = todayAnalytics.getCaloriesBurned() != null ? todayAnalytics.getCaloriesBurned().intValue() : 0;
        int dailyActiveMinutes = todayAnalytics.getActiveMinutes() != null ? todayAnalytics.getActiveMinutes() : 0;
        int dailyWater = todayAnalytics.getWaterIntakeMl() != null ? todayAnalytics.getWaterIntakeMl() : 0;
        int targetWater = todayAnalytics.getTargetWaterMl() != null ? todayAnalytics.getTargetWaterMl() : 2500;

        return new AnalyticsSummaryResponse(
                dailySteps,
                dailyCalories,
                dailyActiveMinutes,
                weeklyActiveMinutes,
                workoutsThisWeek,
                primaryCategory,
                dailyWater,
                targetWater
        );
    }

    public void saveDaily(String email, DailyAnalyticsRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now();
        DailyAnalyticsEntity analytics = analyticsRepository.findByUserIdAndDate(user.getId(), today)
                .orElseGet(() -> {
                    DailyAnalyticsEntity entity = new DailyAnalyticsEntity();
                    entity.setUser(user);
                    entity.setDate(today);
                    return entity;
                });

        if (request.getSteps() != null) analytics.setSteps(request.getSteps());
        if (request.getCaloriesBurned() != null) analytics.setCaloriesBurned(request.getCaloriesBurned());
        if (request.getActiveMinutes() != null) analytics.setActiveMinutes(request.getActiveMinutes());
        if (request.getWaterIntakeMl() != null) analytics.setWaterIntakeMl(request.getWaterIntakeMl());
        if (request.getTargetWaterMl() != null) analytics.setTargetWaterMl(request.getTargetWaterMl());

        analyticsRepository.save(analytics);
    }

    public WaterIntakeResponse getWaterIntake(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now();
        DailyAnalyticsEntity analytics = analyticsRepository.findByUserIdAndDate(user.getId(), today)
                .orElseGet(() -> {
                    DailyAnalyticsEntity entity = new DailyAnalyticsEntity();
                    entity.setUser(user);
                    entity.setDate(today);
                    return analyticsRepository.save(entity);
                });

        return mapToWaterResponse(analytics);
    }

    public WaterIntakeResponse updateWaterIntake(String email, WaterIntakeRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now();
        DailyAnalyticsEntity analytics = analyticsRepository.findByUserIdAndDate(user.getId(), today)
                .orElseGet(() -> {
                    DailyAnalyticsEntity entity = new DailyAnalyticsEntity();
                    entity.setUser(user);
                    entity.setDate(today);
                    return entity;
                });

        if (request.getTargetWaterMl() != null && request.getTargetWaterMl() > 0) {
            analytics.setTargetWaterMl(request.getTargetWaterMl());
        }

        String action = request.getAction() != null ? request.getAction().toUpperCase() : "ADD";
        int amount = request.getAmountMl() != null ? request.getAmountMl() : 0;

        switch (action) {
            case "SET":
                analytics.setWaterIntakeMl(Math.max(0, amount));
                break;
            case "RESET":
                analytics.setWaterIntakeMl(0);
                break;
            case "ADD":
            default:
                analytics.setWaterIntakeMl(Math.max(0, analytics.getWaterIntakeMl() + amount));
                break;
        }

        DailyAnalyticsEntity saved = analyticsRepository.save(analytics);
        return mapToWaterResponse(saved);
    }

    private WaterIntakeResponse mapToWaterResponse(DailyAnalyticsEntity entity) {
        int current = entity.getWaterIntakeMl() != null ? entity.getWaterIntakeMl() : 0;
        int target = entity.getTargetWaterMl() != null ? entity.getTargetWaterMl() : 2500;
        double percentage = target > 0 ? Math.min(100.0, Math.round((current * 100.0 / target) * 100.0) / 100.0) : 0.0;

        return new WaterIntakeResponse(current, target, percentage, entity.getDate().toString());
    }
}
