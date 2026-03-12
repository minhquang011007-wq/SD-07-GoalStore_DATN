package com.example.demo.loyalty.dto;

import lombok.Data;

@Data
public class BirthdayNotificationRequest {

    private Integer customerId;

    private String channel;

    private String note;

}