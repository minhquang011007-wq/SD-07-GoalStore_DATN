package com.example.demo.customer.dto;

import java.time.LocalDateTime;

public class InactiveCustomerResponse {

    private Integer customerId;
    private String ten;
    private String email;
    private String sdt;
    private LocalDateTime lastOrderDate;
    private Long soNgayKhongMua;

    public InactiveCustomerResponse() {
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public LocalDateTime getLastOrderDate() {
        return lastOrderDate;
    }

    public void setLastOrderDate(LocalDateTime lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }

    public Long getSoNgayKhongMua() {
        return soNgayKhongMua;
    }

    public void setSoNgayKhongMua(Long soNgayKhongMua) {
        this.soNgayKhongMua = soNgayKhongMua;
    }
}