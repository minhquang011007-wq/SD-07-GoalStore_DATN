package com.example.demo.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InactiveCustomerResponse {

    private Integer customerId;
    private String ten;
    private String email;
    private String sdt;
    private String loaiKhach;
    private Integer tongDonHang;
    private BigDecimal tongChiTieu;
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

    public String getLoaiKhach() {
        return loaiKhach;
    }

    public void setLoaiKhach(String loaiKhach) {
        this.loaiKhach = loaiKhach;
    }

    public Integer getTongDonHang() {
        return tongDonHang;
    }

    public void setTongDonHang(Integer tongDonHang) {
        this.tongDonHang = tongDonHang;
    }

    public BigDecimal getTongChiTieu() {
        return tongChiTieu;
    }

    public void setTongChiTieu(BigDecimal tongChiTieu) {
        this.tongChiTieu = tongChiTieu;
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