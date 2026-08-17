package com.sonoou.alphagym.dto;

public class WaterIntakeRequest {

    private Integer amountMl;
    private Integer targetWaterMl;
    private String action; // "ADD", "SET", "RESET"

    public WaterIntakeRequest() {}

    public WaterIntakeRequest(Integer amountMl, Integer targetWaterMl, String action) {
        this.amountMl = amountMl;
        this.targetWaterMl = targetWaterMl;
        this.action = action;
    }

    public Integer getAmountMl() { return amountMl; }
    public void setAmountMl(Integer amountMl) { this.amountMl = amountMl; }

    public Integer getTargetWaterMl() { return targetWaterMl; }
    public void setTargetWaterMl(Integer targetWaterMl) { this.targetWaterMl = targetWaterMl; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
