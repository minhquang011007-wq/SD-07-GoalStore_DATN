package com.example.demo.dashboard.dto;

import java.math.BigDecimal;

public class TopCustomerMetric {
    private Integer customerId;
    private String customerName;
    private String loaiKhach;
    private Long orderCount;
    private BigDecimal totalSpent;
    private Integer loyaltyPoints;

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getLoaiKhach() { return loaiKhach; }
    public void setLoaiKhach(String loaiKhach) { this.loaiKhach = loaiKhach; }
    public Long getOrderCount() { return orderCount; }
    public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }
    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
}
