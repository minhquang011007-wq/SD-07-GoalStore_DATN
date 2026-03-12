package com.example.demo.order_return.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ReturnResponse {
    private Integer id;
    private Integer orderId;
    private String reason;
    private String note;
    private BigDecimal refundTotal;
    private LocalDateTime returnDate;
}