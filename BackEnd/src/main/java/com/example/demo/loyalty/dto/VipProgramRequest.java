package com.example.demo.loyalty.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VipProgramRequest {

    private String levelName;

    private Integer minPoints;

    private BigDecimal minSpending;

    private String benefit;

    private Boolean isActive;

}