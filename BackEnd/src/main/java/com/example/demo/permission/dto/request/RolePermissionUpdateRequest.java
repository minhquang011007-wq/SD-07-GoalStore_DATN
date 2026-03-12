package com.example.demo.permission.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class RolePermissionUpdateRequest {

    @NotBlank(message = "Role không được để trống")
    private String role;

    @NotEmpty(message = "Danh sách permission không được để trống")
    private List<String> permissionCodes;

    public RolePermissionUpdateRequest() {
    }

    public String getRole() {
        return role;
    }

    public List<String> getPermissionCodes() {
        return permissionCodes;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes;
    }
}