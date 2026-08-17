package com.sonoou.alphagym.dto;

import java.util.List;

public class ScheduleDto {

    private String dayOfWeek;
    private List<String> workoutIds;
    private String focusArea;
    private String notes;

    public ScheduleDto() {}

    public ScheduleDto(String dayOfWeek, List<String> workoutIds, String focusArea, String notes) {
        this.dayOfWeek = dayOfWeek;
        this.workoutIds = workoutIds;
        this.focusArea = focusArea;
        this.notes = notes;
    }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public List<String> getWorkoutIds() { return workoutIds; }
    public void setWorkoutIds(List<String> workoutIds) { this.workoutIds = workoutIds; }

    public String getFocusArea() { return focusArea; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
