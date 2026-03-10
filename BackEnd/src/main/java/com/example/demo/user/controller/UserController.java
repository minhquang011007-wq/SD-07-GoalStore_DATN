package com.example.demo.user.controller;

import com.example.demo.user.dto.request.ResetPasswordRequest;
import com.example.demo.user.dto.request.UserCreateRequest;
import com.example.demo.user.dto.request.UserStatusRequest;
import com.example.demo.user.dto.request.UserUpdateRequest;
import com.example.demo.user.dto.response.UserResponse;
import com.example.demo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
@Tag(name = "User Management", description = "API quản lý tài khoản người dùng")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Lấy danh sách user", description = "Tìm kiếm, lọc và phân trang danh sách user")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "Từ khóa tìm theo username hoặc email")
            @RequestParam(required = false) String q,

            @Parameter(description = "Lọc theo role: ADMIN / SALES / INVENTORY")
            @RequestParam(required = false) String role,

            @Parameter(description = "Lọc theo trạng thái: ACTIVE / INACTIVE / LOCKED")
            @RequestParam(required = false) String trangThai,

            @Parameter(description = "Trang hiện tại")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số phần tử mỗi trang")
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getAllUsers(q, role, trangThai, page, size));
    }

    @Operation(summary = "Lấy chi tiết user theo id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID user") @PathVariable Integer id
    ) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Tạo user mới")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @Operation(summary = "Cập nhật thông tin user")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID user") @PathVariable Integer id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @Operation(summary = "Đổi trạng thái user")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> changeStatus(
            @Parameter(description = "ID user") @PathVariable Integer id,
            @Valid @RequestBody UserStatusRequest request
    ) {
        return ResponseEntity.ok(userService.changeStatus(id, request));
    }

    @Operation(summary = "Reset mật khẩu user")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<String> resetPassword(
            @Parameter(description = "ID user") @PathVariable Integer id,
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        userService.resetPassword(id, request);
        return ResponseEntity.ok("Reset mật khẩu thành công");
    }

    @Operation(summary = "Xóa user")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "ID user") @PathVariable Integer id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Xóa user thành công");
    }
}