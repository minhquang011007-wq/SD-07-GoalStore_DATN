package com.example.demo.customer.dto;

import java.math.BigDecimal;

public class CustomerStatsResponse {

    private Integer totalCustomers;
    private Integer totalVipCustomers;
    private Integer totalRegularCustomers;
    private Integer totalPoints;
    private BigDecimal totalRevenueFromCustomers;
    private Integer active30Days;

    public CustomerStatsResponse() {
    }

    public Integer getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(Integer totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public Integer getTotalVipCustomers() {
        return totalVipCustomers;
    }

    public void setTotalVipCustomers(Integer totalVipCustomers) {
        this.totalVipCustomers = totalVipCustomers;
    }

    public Integer getTotalRegularCustomers() {
        return totalRegularCustomers;
    }

    public void setTotalRegularCustomers(Integer totalRegularCustomers) {
        this.totalRegularCustomers = totalRegularCustomers;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public BigDecimal getTotalRevenueFromCustomers() {
        return totalRevenueFromCustomers;
    }

    public void setTotalRevenueFromCustomers(BigDecimal totalRevenueFromCustomers) {
        this.totalRevenueFromCustomers = totalRevenueFromCustomers;
    }

    public Integer getActive30Days() {
        return active30Days;
    }

    public void setActive30Days(Integer active30Days) {
        this.active30Days = active30Days;
    }
}