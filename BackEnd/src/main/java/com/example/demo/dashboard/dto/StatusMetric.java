package com.example.demo.dashboard.dto;

public class StatusMetric {
    private String status;
    private String label;
    private Long count;

    public StatusMetric() {
    }

    public StatusMetric(String status, String label, Long count) {
        this.status = status;
        this.label = label;
        this.count = count;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}
