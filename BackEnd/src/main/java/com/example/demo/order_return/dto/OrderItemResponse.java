package com.example.demo.order_return.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Integer itemId;
    private Integer productId;
    private Integer variantId;
    private String sku;
    private String productName;
    private String imageUrl;
    private String size;
    private String color;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}