package com.example.demo.customer.dto;

import java.math.BigDecimal;

public class CustomerFilterRequest {

    private String keyword;
    private String loaiKhach;

    private Integer inactiveDays;

    private Integer minPoints;
    private Integer maxPoints;

    private BigDecimal minSpending;
    private BigDecimal maxSpending;

    private String sortBy;

    public CustomerFilterRequest() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLoaiKhach() {
        return loaiKhach;
    }

    public void setLoaiKhach(String loaiKhach) {
        this.loaiKhach = loaiKhach;
    }

    public Integer getInactiveDays() {
        return inactiveDays;
    }

    public void setInactiveDays(Integer inactiveDays) {
        this.inactiveDays = inactiveDays;
    }

    public Integer getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(Integer minPoints) {
        this.minPoints = minPoints;
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }

    public BigDecimal getMinSpending() {
        return minSpending;
    }

    public void setMinSpending(BigDecimal minSpending) {
        this.minSpending = minSpending;
    }

    public BigDecimal getMaxSpending() {
        return maxSpending;
    }

    public void setMaxSpending(BigDecimal maxSpending) {
        this.maxSpending = maxSpending;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}