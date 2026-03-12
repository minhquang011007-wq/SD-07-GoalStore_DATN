package com.example.demo.permission.service;

import com.example.demo.permission.dto.request.RolePermissionUpdateRequest;
import com.example.demo.permission.dto.response.PermissionResponse;

import java.util.List;
import java.util.Map;

public interface PermissionService {

    List<PermissionResponse> getAllPermissions();

    List<PermissionResponse> getPermissionsByModule(String module);

    List<PermissionResponse> getPermissionsByRole(String role);

    Map<String, Object> updateRolePermissions(RolePermissionUpdateRequest request);

    Map<String, Object> grantPermission(String role, String permissionCode);

    Map<String, Object> revokePermission(String role, String permissionCode);
}