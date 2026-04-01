package com.example.demo.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerResponse {

    private Integer id;
    private String ten;
    private String email;
    private String sdt;
    private LocalDate ngaySinh;
    private String loaiKhach;
    private Integer diemTichLuy;
    private String ghiChu;
    private LocalDateTime createdAt;

    private Integer tongDonHang;
    private BigDecimal tongChiTieu;
    private LocalDateTime lanMuaCuoi;
    private Long soNgayKhongMua;

    public CustomerResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
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

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public Long getSoNgayKhongMua() {
        return soNgayKhongMua;
    }

    public void setSoNgayKhongMua(Long soNgayKhongMua) {
        this.soNgayKhongMua = soNgayKhongMua;
    }
}