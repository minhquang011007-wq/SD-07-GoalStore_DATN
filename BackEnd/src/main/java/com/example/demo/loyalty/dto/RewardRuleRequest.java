package com.example.demo.loyalty.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RewardRuleRequest {

    private String rewardName;

    private Integer requiredPoints;

    private BigDecimal discountValue;

    private String description;

    private Boolean isActive;

}