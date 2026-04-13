package com.example.demo.dashboard.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CustomerDashboardResponse {
    private Integer customerId;
    private String customerName;
    private String loaiKhach;
    private Integer loyaltyPoints;
    private Summary summary = new Summary();
    private VoucherWallet wallet = new VoucherWallet();
    private List<MonthlyMetric> spendingByMonth = new ArrayList<>();
    private List<StatusMetric> orderStatusBreakdown = new ArrayList<>();

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getLoaiKhach() { return loaiKhach; }
    public void setLoaiKhach(String loaiKhach) { this.loaiKhach = loaiKhach; }
    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    public Summary getSummary() { return summary; }
    public void setSummary(Summary summary) { this.summary = summary; }
    public VoucherWallet getWallet() { return wallet; }
    public void setWallet(VoucherWallet wallet) { this.wallet = wallet; }
    public List<MonthlyMetric> getSpendingByMonth() { return spendingByMonth; }
    public void setSpendingByMonth(List<MonthlyMetric> spendingByMonth) { this.spendingByMonth = spendingByMonth; }
    public List<StatusMetric> getOrderStatusBreakdown() { return orderStatusBreakdown; }
    public void setOrderStatusBreakdown(List<StatusMetric> orderStatusBreakdown) { this.orderStatusBreakdown = orderStatusBreakdown; }

    public static class Summary {
        private long totalOrders;
        private long completedOrders;
        private long cancelledOrders;
        private long returnOrders;
        private BigDecimal totalSpent = BigDecimal.ZERO;
        private BigDecimal averageOrderValue = BigDecimal.ZERO;

        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
        public long getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(long completedOrders) { this.completedOrders = completedOrders; }
        public long getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(long cancelledOrders) { this.cancelledOrders = cancelledOrders; }
        public long getReturnOrders() { return returnOrders; }
        public void setReturnOrders(long returnOrders) { this.returnOrders = returnOrders; }
        public BigDecimal getTotalSpent() { return totalSpent; }
        public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
    }

    public static class VoucherWallet {
        private long claimed;
        private long used;
        private long available;

        public long getClaimed() { return claimed; }
        public void setClaimed(long claimed) { this.claimed = claimed; }
        public long getUsed() { return used; }
        public void setUsed(long used) { this.used = used; }
        public long getAvailable() { return available; }
        public void setAvailable(long available) { this.available = available; }
    }
}
