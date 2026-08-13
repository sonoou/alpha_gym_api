package com.sonoou.alphagym.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;
    private Integer age;
    private Double weight;
    private Double height;
    private String gender;
    private String fitnessGoal;

    @Column(nullable = false)
    private Boolean onboardingCompleted = false;

    private String profilePhotoUrl;

    @Column(nullable = false)
    private Integer currentStreakDays = 0;

    @Column(nullable = false)
    private Integer totalWorkoutsCompleted = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_routines", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "workout_id")
    private Set<Long> bookmarkedWorkoutIds = new HashSet<>();

    private String activePlanName;
    private LocalDateTime planExpiryDate;

    @Column(nullable = false)
    private Boolean membershipActive = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UserEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getFitnessGoal() { return fitnessGoal; }
    public void setFitnessGoal(String fitnessGoal) { this.fitnessGoal = fitnessGoal; }

    public Boolean getOnboardingCompleted() { return onboardingCompleted; }
    public void setOnboardingCompleted(Boolean onboardingCompleted) { this.onboardingCompleted = onboardingCompleted; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public Integer getCurrentStreakDays() { return currentStreakDays; }
    public void setCurrentStreakDays(Integer currentStreakDays) { this.currentStreakDays = currentStreakDays; }

    public Integer getTotalWorkoutsCompleted() { return totalWorkoutsCompleted; }
    public void setTotalWorkoutsCompleted(Integer totalWorkoutsCompleted) { this.totalWorkoutsCompleted = totalWorkoutsCompleted; }

    public Set<Long> getBookmarkedWorkoutIds() { return bookmarkedWorkoutIds; }
    public void setBookmarkedWorkoutIds(Set<Long> bookmarkedWorkoutIds) { this.bookmarkedWorkoutIds = bookmarkedWorkoutIds; }

    public String getActivePlanName() { return activePlanName; }
    public void setActivePlanName(String activePlanName) { this.activePlanName = activePlanName; }

    public LocalDateTime getPlanExpiryDate() { return planExpiryDate; }
    public void setPlanExpiryDate(LocalDateTime planExpiryDate) { this.planExpiryDate = planExpiryDate; }

    public Boolean getMembershipActive() {
        if (planExpiryDate != null && LocalDateTime.now().isAfter(planExpiryDate)) {
            return false;
        }
        return membershipActive != null ? membershipActive : false;
    }

    public void setMembershipActive(Boolean membershipActive) { this.membershipActive = membershipActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
