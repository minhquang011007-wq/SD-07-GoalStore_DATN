package com.example.demo.loyalty.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RedeemRewardResponse {

    private Integer customerId;
    private String customerName;
    private Integer rewardRuleId;
    private String rewardName;
    private Integer requiredPoints;
    private BigDecimal discountValue;
    private Integer remainingPoints;
    private LocalDateTime redeemedAt;
    private String note;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getRewardRuleId() {
        return rewardRuleId;
    }

    public void setRewardRuleId(Integer rewardRuleId) {
        this.rewardRuleId = rewardRuleId;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public Integer getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(Integer requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public Integer getRemainingPoints() {
        return remainingPoints;
    }

    public void setRemainingPoints(Integer remainingPoints) {
        this.remainingPoints = remainingPoints;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}