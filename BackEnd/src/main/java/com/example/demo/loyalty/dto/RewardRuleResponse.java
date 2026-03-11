package com.example.demo.loyalty.dto;

import java.math.BigDecimal;

public class RewardRuleResponse {

    private Integer id;
    private String rewardName;
    private Integer requiredPoints;
    private BigDecimal discountValue;
    private String description;
    private Boolean isActive;

    public RewardRuleResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}