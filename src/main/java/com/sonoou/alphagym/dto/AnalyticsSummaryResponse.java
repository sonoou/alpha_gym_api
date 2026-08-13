package com.sonoou.alphagym.dto;

public class AnalyticsSummaryResponse {

    private Integer dailySteps;
    private Integer dailyCalories;
    private Integer dailyActiveMinutes;
    private Integer weeklyTotalMinutes;
    private Integer workoutsThisWeek;
    private String primaryCategoryFocus;

    private Integer dailyWaterIntakeMl;
    private Integer targetWaterMl;

    public AnalyticsSummaryResponse() {}

    public AnalyticsSummaryResponse(Integer dailySteps, Integer dailyCalories, Integer dailyActiveMinutes,
                                    Integer weeklyTotalMinutes, Integer workoutsThisWeek, String primaryCategoryFocus,
                                    Integer dailyWaterIntakeMl, Integer targetWaterMl) {
        this.dailySteps = dailySteps;
        this.dailyCalories = dailyCalories;
        this.dailyActiveMinutes = dailyActiveMinutes;
        this.weeklyTotalMinutes = weeklyTotalMinutes;
        this.workoutsThisWeek = workoutsThisWeek;
        this.primaryCategoryFocus = primaryCategoryFocus;
        this.dailyWaterIntakeMl = dailyWaterIntakeMl;
        this.targetWaterMl = targetWaterMl;
    }

    public Integer getDailySteps() { return dailySteps; }
    public void setDailySteps(Integer dailySteps) { this.dailySteps = dailySteps; }

    public Integer getDailyCalories() { return dailyCalories; }
    public void setDailyCalories(Integer dailyCalories) { this.dailyCalories = dailyCalories; }

    public Integer getDailyActiveMinutes() { return dailyActiveMinutes; }
    public void setDailyActiveMinutes(Integer dailyActiveMinutes) { this.dailyActiveMinutes = dailyActiveMinutes; }

    public Integer getWeeklyTotalMinutes() { return weeklyTotalMinutes; }
    public void setWeeklyTotalMinutes(Integer weeklyTotalMinutes) { this.weeklyTotalMinutes = weeklyTotalMinutes; }

    public Integer getWorkoutsThisWeek() { return workoutsThisWeek; }
    public void setWorkoutsThisWeek(Integer workoutsThisWeek) { this.workoutsThisWeek = workoutsThisWeek; }

    public String getPrimaryCategoryFocus() { return primaryCategoryFocus; }
    public void setPrimaryCategoryFocus(String primaryCategoryFocus) { this.primaryCategoryFocus = primaryCategoryFocus; }

    public Integer getDailyWaterIntakeMl() { return dailyWaterIntakeMl; }
    public void setDailyWaterIntakeMl(Integer dailyWaterIntakeMl) { this.dailyWaterIntakeMl = dailyWaterIntakeMl; }

    public Integer getTargetWaterMl() { return targetWaterMl; }
    public void setTargetWaterMl(Integer targetWaterMl) { this.targetWaterMl = targetWaterMl; }
}
