package com.example.demo.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerActivityResponse {

    private Integer customerId;
    private String ten;
    private String loaiKhach;
    private LocalDateTime lastOrderDate;
    private Integer soDonTrongKy;
    private BigDecimal chiTieuTrongKy;

    public CustomerActivityResponse() {
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

    public String getLoaiKhach() {
        return loaiKhach;
    }

    public void setLoaiKhach(String loaiKhach) {
        this.loaiKhach = loaiKhach;
    }

    public LocalDateTime getLastOrderDate() {
        return lastOrderDate;
    }

    public void setLastOrderDate(LocalDateTime lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }

    public Integer getSoDonTrongKy() {
        return soDonTrongKy;
    }

    public void setSoDonTrongKy(Integer soDonTrongKy) {
        this.soDonTrongKy = soDonTrongKy;
    }

    public BigDecimal getChiTieuTrongKy() {
        return chiTieuTrongKy;
    }

    public void setChiTieuTrongKy(BigDecimal chiTieuTrongKy) {
        this.chiTieuTrongKy = chiTieuTrongKy;
    }
}