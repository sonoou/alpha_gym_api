package com.sonoou.alphagym.dto;

public class WaterIntakeResponse {

    private Integer waterIntakeMl;
    private Integer targetWaterMl;
    private Double percentage;
    private String date;

    public WaterIntakeResponse() {}

    public WaterIntakeResponse(Integer waterIntakeMl, Integer targetWaterMl, Double percentage, String date) {
        this.waterIntakeMl = waterIntakeMl;
        this.targetWaterMl = targetWaterMl;
        this.percentage = percentage;
        this.date = date;
    }

    public Integer getWaterIntakeMl() { return waterIntakeMl; }
    public void setWaterIntakeMl(Integer waterIntakeMl) { this.waterIntakeMl = waterIntakeMl; }

    public Integer getTargetWaterMl() { return targetWaterMl; }
    public void setTargetWaterMl(Integer targetWaterMl) { this.targetWaterMl = targetWaterMl; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
