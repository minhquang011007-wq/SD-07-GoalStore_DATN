package com.example.demo.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TopLoyalCustomerResponse {

    private Integer customerId;
    private String ten;
    private String email;
    private String sdt;
    private String loaiKhach;
    private Integer diemTichLuy;
    private Integer tongDonHang;
    private BigDecimal tongChiTieu;
    private LocalDateTime lanMuaCuoi;

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

    public Integer getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(Integer diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
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

    public LocalDateTime getLanMuaCuoi() {
        return lanMuaCuoi;
    }

    public void setLanMuaCuoi(LocalDateTime lanMuaCuoi) {
        this.lanMuaCuoi = lanMuaCuoi;
    }
}