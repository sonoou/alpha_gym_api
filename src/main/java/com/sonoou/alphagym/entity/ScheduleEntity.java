package com.sonoou.alphagym.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_schedules", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "day_of_week"})
})
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    private String focusArea;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "schedule_workout_ids", joinColumns = @JoinColumn(name = "schedule_id"))
    @Column(name = "workout_id")
    private List<String> workoutIds = new ArrayList<>();

    public ScheduleEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getFocusArea() { return focusArea; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<String> getWorkoutIds() { return workoutIds; }
    public void setWorkoutIds(List<String> workoutIds) { this.workoutIds = workoutIds; }
}
