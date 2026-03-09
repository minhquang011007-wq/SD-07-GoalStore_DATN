package com.example.demo.user.controller;

import com.example.demo.user.dto.request.ResetPasswordRequest;
import com.example.demo.user.dto.request.UserCreateRequest;
import com.example.demo.user.dto.request.UserStatusRequest;
import com.example.demo.user.dto.request.UserUpdateRequest;
import com.example.demo.user.dto.response.UserResponse;
import com.example.demo.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getAllUsers(q, role, trangThai, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> changeStatus(
            @PathVariable Integer id,
            @Valid @RequestBody UserStatusRequest request
    ) {
        return ResponseEntity.ok(userService.changeStatus(id, request));
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<String> resetPassword(
            @PathVariable Integer id,
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        userService.resetPassword(id, request);
        return ResponseEntity.ok("Reset mật khẩu thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Xóa user thành công");
    }
}