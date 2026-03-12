package com.example.demo.order.dto;

public class UpdateOrderItemRequest {
    private Integer quantity;

    public UpdateOrderItemRequest() {
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}