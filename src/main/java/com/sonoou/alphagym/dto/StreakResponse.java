package com.sonoou.alphagym.dto;

public class StreakResponse {

    private Integer currentStreakDays;
    private Integer totalWorkoutsCompleted;

    public StreakResponse() {}

    public StreakResponse(Integer currentStreakDays, Integer totalWorkoutsCompleted) {
        this.currentStreakDays = currentStreakDays;
        this.totalWorkoutsCompleted = totalWorkoutsCompleted;
    }

    public Integer getCurrentStreakDays() { return currentStreakDays; }
    public void setCurrentStreakDays(Integer currentStreakDays) { this.currentStreakDays = currentStreakDays; }

    public Integer getTotalWorkoutsCompleted() { return totalWorkoutsCompleted; }
    public void setTotalWorkoutsCompleted(Integer totalWorkoutsCompleted) { this.totalWorkoutsCompleted = totalWorkoutsCompleted; }
}
