package com.sonoou.alphagym.dto;

public class RazorpayOrderResponse {

    private String orderId;
    private String keyId;
    private Integer amountInPaisa;
    private String currency;
    private String status;

    public RazorpayOrderResponse() {}

    public RazorpayOrderResponse(String orderId, String keyId, Integer amountInPaisa, String currency, String status) {
        this.orderId = orderId;
        this.keyId = keyId;
        this.amountInPaisa = amountInPaisa;
        this.currency = currency;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }

    public Integer getAmountInPaisa() { return amountInPaisa; }
    public void setAmountInPaisa(Integer amountInPaisa) { this.amountInPaisa = amountInPaisa; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
