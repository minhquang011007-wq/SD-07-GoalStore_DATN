package com.example.demo.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserCreateRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(max = 50)
    private String username;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "Role không được để trống")
    private String role;

    @NotBlank(message = "Trạng thái không được để trống")
    private String trangThai;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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