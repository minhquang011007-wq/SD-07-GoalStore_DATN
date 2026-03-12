package com.example.demo.order_return.dto;

import lombok.Data;

@Data
public class CreateReturnRequest {
    private String reason;
    private String note;
}