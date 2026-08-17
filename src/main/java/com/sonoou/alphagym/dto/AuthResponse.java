package com.sonoou.alphagym.dto;

public class AuthResponse {

    private String token;
    private String name;
    private String email;
    private Boolean onboardingCompleted;

    public AuthResponse() {}

    public AuthResponse(String token, String name, String email, Boolean onboardingCompleted) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.onboardingCompleted = onboardingCompleted;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getOnboardingCompleted() { return onboardingCompleted; }
    public void setOnboardingCompleted(Boolean onboardingCompleted) { this.onboardingCompleted = onboardingCompleted; }
}
