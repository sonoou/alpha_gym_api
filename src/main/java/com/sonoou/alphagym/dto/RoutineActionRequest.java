package com.sonoou.alphagym.dto;

public class RoutineActionRequest {

    private Long workoutId;
    private String action; // 'ADD' or 'REMOVE'

    public RoutineActionRequest() {}

    public Long getWorkoutId() { return workoutId; }
    public void setWorkoutId(Long workoutId) { this.workoutId = workoutId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
