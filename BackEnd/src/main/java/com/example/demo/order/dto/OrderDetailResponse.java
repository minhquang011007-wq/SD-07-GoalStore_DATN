package com.example.demo.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailResponse {

    private Integer id;
    private Integer customerId;
    private String customerName;
    private LocalDateTime orderDate;
    private String status;
    private String paymentMethod;
    private String channel;
    private BigDecimal total;
    private Integer totalItems;
    private List<OrderItemDetailResponse> items;
    private ReturnResponse returnInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public List<OrderItemDetailResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDetailResponse> items) {
        this.items = items;
    }

    public ReturnResponse getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(ReturnResponse returnInfo) {
        this.returnInfo = returnInfo;
    }
}