package com.sonoou.alphagym.dto;

import jakarta.validation.constraints.NotNull;

public class CreateOrderRequest {

    @NotNull
    private Double amount;
    private String currency;
    private String receipt;

    public CreateOrderRequest() {}

    public CreateOrderRequest(Double amount, String currency, String receipt) {
        this.amount = amount;
        this.currency = currency;
        this.receipt = receipt;
    }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getReceipt() { return receipt; }
    public void setReceipt(String receipt) { this.receipt = receipt; }
}
