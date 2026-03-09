package com.example.demo.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String email;

    private String role;

    private String trangThai;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}