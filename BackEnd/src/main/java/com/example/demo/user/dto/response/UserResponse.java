package com.example.demo.user.dto.response;

import java.time.LocalDateTime;

public class UserResponse {

    private Integer id;
    private String username;
    private String email;
    private String role;
    private String trangThai;
    private LocalDateTime createdAt;

    public UserResponse() {
    }

    public UserResponse(Integer id, String username, String email, String role, String trangThai, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.trangThai = trangThai;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}