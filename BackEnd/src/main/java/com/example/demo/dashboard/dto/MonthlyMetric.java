package com.example.demo.dashboard.dto;

import java.math.BigDecimal;

public class MonthlyMetric {
    private Integer month;
    private String label;
    private BigDecimal value;

    public MonthlyMetric() {
    }

    public MonthlyMetric(Integer month, String label, BigDecimal value) {
        this.month = month;
        this.label = label;
        this.value = value;
    }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
}
