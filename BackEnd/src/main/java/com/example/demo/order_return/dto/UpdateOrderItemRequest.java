package com.example.demo.order_return.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderItemRequest {

    @NotNull
    @Min(1)
    private Integer quantity;
}