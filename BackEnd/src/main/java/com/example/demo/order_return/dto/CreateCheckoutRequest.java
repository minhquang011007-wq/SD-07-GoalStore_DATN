package com.example.demo.order_return.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCheckoutRequest {

    @NotNull
    private Integer customerId;

    private Integer addressId;
    private String paymentMethod;
    private String note;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
}
