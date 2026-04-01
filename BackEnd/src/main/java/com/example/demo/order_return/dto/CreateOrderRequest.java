package com.example.demo.order_return.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull
    private Integer customerId;

    private Integer staffId;
    private String paymentMethod;
    private String paymentStatus;
    private String channel;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String note;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;

    @NotEmpty
    private List<CreateOrderItemRequest> items;
}
