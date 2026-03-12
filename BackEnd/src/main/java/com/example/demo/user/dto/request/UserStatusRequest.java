package com.example.demo.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UserStatusRequest {

    @NotBlank(message = "Trạng thái không được để trống")
    private String trangThai;

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}