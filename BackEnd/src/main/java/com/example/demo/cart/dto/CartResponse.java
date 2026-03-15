package com.example.demo.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private Integer cartId;
    private Integer customerId;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;

    public Integer getCartId() { return cartId; }
    public void setCartId(Integer cartId) { this.cartId = cartId; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }
}
