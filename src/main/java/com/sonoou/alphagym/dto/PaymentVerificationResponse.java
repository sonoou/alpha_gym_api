package com.sonoou.alphagym.dto;

public class PaymentVerificationResponse {

    private String status;
    private String message;
    private String paymentId;

    public PaymentVerificationResponse() {}

    public PaymentVerificationResponse(String status, String message, String paymentId) {
        this.status = status;
        this.message = message;
        this.paymentId = paymentId;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
}
