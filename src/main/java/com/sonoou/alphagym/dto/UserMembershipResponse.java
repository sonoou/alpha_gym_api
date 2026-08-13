package com.sonoou.alphagym.dto;

public class UserMembershipResponse {

    private Boolean isMembershipActive;
    private String activePlanName;
    private String planStartDate;
    private String planExpiryDate;
    private Long daysRemaining;

    public UserMembershipResponse() {}

    public UserMembershipResponse(Boolean isMembershipActive, String activePlanName, String planStartDate, String planExpiryDate, Long daysRemaining) {
        this.isMembershipActive = isMembershipActive;
        this.activePlanName = activePlanName;
        this.planStartDate = planStartDate;
        this.planExpiryDate = planExpiryDate;
        this.daysRemaining = daysRemaining;
    }

    public Boolean getIsMembershipActive() { return isMembershipActive; }
    public void setIsMembershipActive(Boolean isMembershipActive) { this.isMembershipActive = isMembershipActive; }

    public String getActivePlanName() { return activePlanName; }
    public void setActivePlanName(String activePlanName) { this.activePlanName = activePlanName; }

    public String getPlanStartDate() { return planStartDate; }
    public void setPlanStartDate(String planStartDate) { this.planStartDate = planStartDate; }

    public String getPlanExpiryDate() { return planExpiryDate; }
    public void setPlanExpiryDate(String planExpiryDate) { this.planExpiryDate = planExpiryDate; }

    public Long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(Long daysRemaining) { this.daysRemaining = daysRemaining; }
}
