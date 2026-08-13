package com.sonoou.alphagym.dto;

public class CreateOrderRequest {

    private Long planId;
    private Double amount;
    private String currency;
    private String receipt;

    public CreateOrderRequest() {}

    public CreateOrderRequest(Long planId, Double amount, String currency, String receipt) {
        this.planId = planId;
        this.amount = amount;
        this.currency = currency;
        this.receipt = receipt;
    }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getReceipt() { return receipt; }
    public void setReceipt(String receipt) { this.receipt = receipt; }
}
