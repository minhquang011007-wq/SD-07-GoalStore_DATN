package com.example.demo.permission.controller;

import com.example.demo.permission.dto.request.RolePermissionUpdateRequest;
import com.example.demo.permission.dto.response.PermissionResponse;
import com.example.demo.permission.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin("*")
@Tag(name = "Role & Permission", description = "API phân quyền theo role")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "Lấy toàn bộ permissions")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @Operation(summary = "Lấy permissions theo module")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/module/{module}")
    public ResponseEntity<List<PermissionResponse>> getPermissionsByModule(
            @Parameter(description = "Tên module, ví dụ USER, ORDER, PRODUCT")
            @PathVariable String module
    ) {
        return ResponseEntity.ok(permissionService.getPermissionsByModule(module));
    }

    @Operation(summary = "Lấy permissions theo role")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<PermissionResponse>> getPermissionsByRole(
            @Parameter(description = "Role: ADMIN / SALES / INVENTORY")
            @PathVariable String role
    ) {
        return ResponseEntity.ok(permissionService.getPermissionsByRole(role));
    }

    @Operation(summary = "Cập nhật toàn bộ quyền của role")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/role")
    public ResponseEntity<Map<String, Object>> updateRolePermissions(
            @Valid @RequestBody RolePermissionUpdateRequest request
    ) {
        return ResponseEntity.ok(permissionService.updateRolePermissions(request));
    }

    @Operation(summary = "Gán thêm 1 permission cho role")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/role/{role}/grant/{permissionCode}")
    public ResponseEntity<Map<String, Object>> grantPermission(
            @PathVariable String role,
            @PathVariable String permissionCode
    ) {
        return ResponseEntity.ok(permissionService.grantPermission(role, permissionCode));
    }

    @Operation(summary = "Thu hồi 1 permission khỏi role")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/role/{role}/revoke/{permissionCode}")
    public ResponseEntity<Map<String, Object>> revokePermission(
            @PathVariable String role,
            @PathVariable String permissionCode
    ) {
        return ResponseEntity.ok(permissionService.revokePermission(role, permissionCode));
    }
}