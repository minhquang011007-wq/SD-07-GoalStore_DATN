package com.example.demo.payment.dto;

public class VnpayCreatePaymentResponse {
    private Integer orderId;
    private String transactionRef;
    private String paymentUrl;
    private String provider;
    private String expiresAt;
    private boolean qrOnly;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isQrOnly() {
        return qrOnly;
    }

    public void setQrOnly(boolean qrOnly) {
        this.qrOnly = qrOnly;
    }
}
