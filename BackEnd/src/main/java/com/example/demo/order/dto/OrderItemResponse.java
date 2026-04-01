package com.example.demo.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {

    private Integer productVariantId;

    private String productName;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal total;

}