package com.example.demo.payment.dto;

public class VnpayProcessResult {
    private boolean validSignature;
    private boolean orderFound;
    private boolean amountValid;
    private boolean successfulPayment;
    private boolean alreadyConfirmed;
    private Integer orderId;
    private String transactionRef;
    private String responseCode;
    private String transactionStatus;
    private String message;

    public boolean isValidSignature() {
        return validSignature;
    }

    public void setValidSignature(boolean validSignature) {
        this.validSignature = validSignature;
    }

    public boolean isOrderFound() {
        return orderFound;
    }

    public void setOrderFound(boolean orderFound) {
        this.orderFound = orderFound;
    }

    public boolean isAmountValid() {
        return amountValid;
    }

    public void setAmountValid(boolean amountValid) {
        this.amountValid = amountValid;
    }

    public boolean isSuccessfulPayment() {
        return successfulPayment;
    }

    public void setSuccessfulPayment(boolean successfulPayment) {
        this.successfulPayment = successfulPayment;
    }

    public boolean isAlreadyConfirmed() {
        return alreadyConfirmed;
    }

    public void setAlreadyConfirmed(boolean alreadyConfirmed) {
        this.alreadyConfirmed = alreadyConfirmed;
    }

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

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
