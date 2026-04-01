package com.example.demo.loyalty.dto;

import lombok.Data;

@Data
public class AssignVipRequest {

    private Integer customerId;
    private String newLevel;
    private String reason;

}