package com.sonoou.alphagym.dto;

public class PaymentHistoryResponse {

    private Long id;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private Double amount;
    private String currency;
    private Long planId;
    private String planName;
    private String status;
    private String paymentDate;
    private String receiptDownloadUrl;

    public PaymentHistoryResponse() {}

    public PaymentHistoryResponse(Long id, String razorpayOrderId, String razorpayPaymentId, Double amount, String currency, Long planId, String planName, String status, String paymentDate, String receiptDownloadUrl) {
        this.id = id;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.amount = amount;
        this.currency = currency;
        this.planId = planId;
        this.planName = planName;
        this.status = status;
        this.paymentDate = paymentDate;
        this.receiptDownloadUrl = receiptDownloadUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public String getReceiptDownloadUrl() { return receiptDownloadUrl; }
    public void setReceiptDownloadUrl(String receiptDownloadUrl) { this.receiptDownloadUrl = receiptDownloadUrl; }
}
