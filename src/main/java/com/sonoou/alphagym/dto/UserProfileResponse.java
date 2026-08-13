package com.sonoou.alphagym.dto;

public class UserProfileResponse {

    private Long id;
    private String email;
    private String name;
    private Integer age;
    private Double weight;
    private Double height;
    private String gender;
    private String fitnessGoal;
    private Boolean onboardingCompleted;
    private String profilePhotoUrl;
    private Integer currentStreakDays;
    private Integer totalWorkoutsCompleted;
    private Boolean isMembershipActive;
    private String activePlanName;
    private String planExpiryDate;

    public UserProfileResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public Boolean getIsMembershipActive() { return isMembershipActive; }
    public void setIsMembershipActive(Boolean isMembershipActive) { this.isMembershipActive = isMembershipActive; }

    public String getActivePlanName() { return activePlanName; }
    public void setActivePlanName(String activePlanName) { this.activePlanName = activePlanName; }

    public String getPlanExpiryDate() { return planExpiryDate; }
    public void setPlanExpiryDate(String planExpiryDate) { this.planExpiryDate = planExpiryDate; }
}
