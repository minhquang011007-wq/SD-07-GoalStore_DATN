package com.example.demo.dashboard.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardResponse {
    private Integer year;
    private Summary summary = new Summary();
    private CustomerSection customers = new CustomerSection();
    private VoucherSection vouchers = new VoucherSection();
    private List<MonthlyMetric> revenueByMonth = new ArrayList<>();
    private List<MonthlyMetric> ordersByMonth = new ArrayList<>();
    private List<StatusMetric> orderStatusBreakdown = new ArrayList<>();
    private List<TopCustomerMetric> topCustomers = new ArrayList<>();

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Summary getSummary() { return summary; }
    public void setSummary(Summary summary) { this.summary = summary; }
    public CustomerSection getCustomers() { return customers; }
    public void setCustomers(CustomerSection customers) { this.customers = customers; }
    public VoucherSection getVouchers() { return vouchers; }
    public void setVouchers(VoucherSection vouchers) { this.vouchers = vouchers; }
    public List<MonthlyMetric> getRevenueByMonth() { return revenueByMonth; }
    public void setRevenueByMonth(List<MonthlyMetric> revenueByMonth) { this.revenueByMonth = revenueByMonth; }
    public List<MonthlyMetric> getOrdersByMonth() { return ordersByMonth; }
    public void setOrdersByMonth(List<MonthlyMetric> ordersByMonth) { this.ordersByMonth = ordersByMonth; }
    public List<StatusMetric> getOrderStatusBreakdown() { return orderStatusBreakdown; }
    public void setOrderStatusBreakdown(List<StatusMetric> orderStatusBreakdown) { this.orderStatusBreakdown = orderStatusBreakdown; }
    public List<TopCustomerMetric> getTopCustomers() { return topCustomers; }
    public void setTopCustomers(List<TopCustomerMetric> topCustomers) { this.topCustomers = topCustomers; }

    public static class Summary {
        private long totalOrders;
        private long completedOrders;
        private long cancelledOrders;
        private long returnOrders;
        private BigDecimal totalRevenue = BigDecimal.ZERO;
        private BigDecimal averageOrderValue = BigDecimal.ZERO;

        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
        public long getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(long completedOrders) { this.completedOrders = completedOrders; }
        public long getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(long cancelledOrders) { this.cancelledOrders = cancelledOrders; }
        public long getReturnOrders() { return returnOrders; }
        public void setReturnOrders(long returnOrders) { this.returnOrders = returnOrders; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
    }

    public static class CustomerSection {
        private long totalCustomers;
        private long vipCustomers;
        private long regularCustomers;
        private long activeCustomers30Days;
        private long newCustomersThisYear;
        private long totalPoints;

        public long getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
        public long getVipCustomers() { return vipCustomers; }
        public void setVipCustomers(long vipCustomers) { this.vipCustomers = vipCustomers; }
        public long getRegularCustomers() { return regularCustomers; }
        public void setRegularCustomers(long regularCustomers) { this.regularCustomers = regularCustomers; }
        public long getActiveCustomers30Days() { return activeCustomers30Days; }
        public void setActiveCustomers30Days(long activeCustomers30Days) { this.activeCustomers30Days = activeCustomers30Days; }
        public long getNewCustomersThisYear() { return newCustomersThisYear; }
        public void setNewCustomersThisYear(long newCustomersThisYear) { this.newCustomersThisYear = newCustomersThisYear; }
        public long getTotalPoints() { return totalPoints; }
        public void setTotalPoints(long totalPoints) { this.totalPoints = totalPoints; }
    }

    public static class VoucherSection {
        private long totalVouchers;
        private long activeVouchers;
        private long claimedVouchers;
        private long usedVouchers;
        private long remainingVouchers;
        private long totalVoucherQuantity;

        public long getTotalVouchers() { return totalVouchers; }
        public void setTotalVouchers(long totalVouchers) { this.totalVouchers = totalVouchers; }
        public long getActiveVouchers() { return activeVouchers; }
        public void setActiveVouchers(long activeVouchers) { this.activeVouchers = activeVouchers; }
        public long getClaimedVouchers() { return claimedVouchers; }
        public void setClaimedVouchers(long claimedVouchers) { this.claimedVouchers = claimedVouchers; }
        public long getUsedVouchers() { return usedVouchers; }
        public void setUsedVouchers(long usedVouchers) { this.usedVouchers = usedVouchers; }
        public long getRemainingVouchers() { return remainingVouchers; }
        public void setRemainingVouchers(long remainingVouchers) { this.remainingVouchers = remainingVouchers; }
        public long getTotalVoucherQuantity() { return totalVoucherQuantity; }
        public void setTotalVoucherQuantity(long totalVoucherQuantity) { this.totalVoucherQuantity = totalVoucherQuantity; }
    }
}
