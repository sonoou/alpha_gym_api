package com.sonoou.alphagym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class CreateOrderRequest {

    private Long planId;
    private Double amount;
    private String currency;
    private String receipt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    public CreateOrderRequest() {}

    public CreateOrderRequest(Long planId, Double amount, String currency, String receipt, LocalDate startDate) {
        this.planId = planId;
        this.amount = amount;
        this.currency = currency;
        this.receipt = receipt;
        this.startDate = startDate;
    }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getReceipt() { return receipt; }
    public void setReceipt(String receipt) { this.receipt = receipt; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
}
