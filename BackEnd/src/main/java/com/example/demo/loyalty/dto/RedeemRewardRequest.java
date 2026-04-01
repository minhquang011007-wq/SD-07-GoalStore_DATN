package com.example.demo.loyalty.dto;

public class RedeemRewardRequest {

    private Integer customerId;
    private Integer rewardRuleId;
    private String note;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getRewardRuleId() {
        return rewardRuleId;
    }

    public void setRewardRuleId(Integer rewardRuleId) {
        this.rewardRuleId = rewardRuleId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}