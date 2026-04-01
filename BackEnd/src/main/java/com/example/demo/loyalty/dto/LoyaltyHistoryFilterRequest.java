package com.example.demo.loyalty.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoyaltyHistoryFilterRequest {

    private Integer customerId;

    private String type;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

}