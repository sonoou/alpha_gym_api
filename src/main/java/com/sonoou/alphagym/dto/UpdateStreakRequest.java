package com.sonoou.alphagym.dto;

public class UpdateStreakRequest {

    private Integer streakDays;
    private Integer totalWorkoutsCompleted;

    public UpdateStreakRequest() {}

    public Integer getStreakDays() { return streakDays; }
    public void setStreakDays(Integer streakDays) { this.streakDays = streakDays; }

    public Integer getTotalWorkoutsCompleted() { return totalWorkoutsCompleted; }
    public void setTotalWorkoutsCompleted(Integer totalWorkoutsCompleted) { this.totalWorkoutsCompleted = totalWorkoutsCompleted; }
}
