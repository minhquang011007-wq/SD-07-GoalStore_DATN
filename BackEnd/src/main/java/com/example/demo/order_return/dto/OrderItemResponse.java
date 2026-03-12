package com.example.demo.order_return.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Integer itemId;
    private Integer variantId;
    private String sku;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}