package com.sonoou.alphagym.dto;

public class DailyAnalyticsRequest {

    private Integer steps;
    private Double caloriesBurned;
    private Integer activeMinutes;
    private Integer waterIntakeMl;
    private Integer targetWaterMl;

    public DailyAnalyticsRequest() {}

    public Integer getSteps() { return steps; }
    public void setSteps(Integer steps) { this.steps = steps; }

    public Double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Double caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public Integer getActiveMinutes() { return activeMinutes; }
    public void setActiveMinutes(Integer activeMinutes) { this.activeMinutes = activeMinutes; }

    public Integer getWaterIntakeMl() { return waterIntakeMl; }
    public void setWaterIntakeMl(Integer waterIntakeMl) { this.waterIntakeMl = waterIntakeMl; }

    public Integer getTargetWaterMl() { return targetWaterMl; }
    public void setTargetWaterMl(Integer targetWaterMl) { this.targetWaterMl = targetWaterMl; }
}
