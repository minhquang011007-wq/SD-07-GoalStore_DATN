package com.example.demo.customer.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten", nullable = false, length = 150)
    private String ten;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "loai_khach", nullable = false, length = 10)
    private String loaiKhach;

    @Column(name = "diem_tich_luy", nullable = false)
    private Integer diemTichLuy;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.loaiKhach == null) this.loaiKhach = "THUONG";
        if (this.diemTichLuy == null) this.diemTichLuy = 0;
        if (this.status == null || this.status.isBlank()) this.status = "ACTIVE";
        if (this.isDeleted == null) this.isDeleted = false;
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.status == null || this.status.isBlank()) this.status = "ACTIVE";
        if (this.isDeleted == null) this.isDeleted = false;
    }

    public Customer() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getLoaiKhach() { return loaiKhach; }
    public void setLoaiKhach(String loaiKhach) { this.loaiKhach = loaiKhach; }
    public Integer getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(Integer diemTichLuy) { this.diemTichLuy = diemTichLuy; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
