package com.example.demo.order_return.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull
    private Integer customerId;

    private Integer staffId;

    private String paymentMethod;

    private String channel;

    @NotEmpty
    private List<CreateOrderItemRequest> items;
}