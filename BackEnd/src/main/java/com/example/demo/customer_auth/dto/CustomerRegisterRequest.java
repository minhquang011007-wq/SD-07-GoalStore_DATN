package com.example.demo.customer_auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.LocalDate;

public class CustomerRegisterRequest {

    @JsonAlias({"fullName", "name"})
    private String ten;

    private String email;

    @JsonAlias({"phone", "phoneNumber"})
    private String sdt;

    private String password;
    private LocalDate ngaySinh;

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getFullName() { return ten; }
    public void setFullName(String fullName) { this.ten = fullName; }
    public String getPhone() { return sdt; }
    public void setPhone(String phone) { this.sdt = phone; }
}
