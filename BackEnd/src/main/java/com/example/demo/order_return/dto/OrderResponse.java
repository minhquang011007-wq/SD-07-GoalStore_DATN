package com.example.demo.order_return.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Integer id;
    private Integer customerId;
    private String customerName;
    private Integer staffId;
    private String staffUsername;
    private String status;
    private String paymentMethod;
    private String channel;
    private BigDecimal total;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
}