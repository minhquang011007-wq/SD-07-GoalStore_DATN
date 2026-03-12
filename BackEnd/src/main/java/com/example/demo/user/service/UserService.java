package com.example.demo.user.service;

import com.example.demo.user.dto.request.ResetPasswordRequest;
import com.example.demo.user.dto.request.UserCreateRequest;
import com.example.demo.user.dto.request.UserStatusRequest;
import com.example.demo.user.dto.request.UserUpdateRequest;
import com.example.demo.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {

    Page<UserResponse> getAllUsers(
            String q,
            String role,
            String trangThai,
            int page,
            int size
    );

    UserResponse getUserById(Integer id);

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateUser(Integer id, UserUpdateRequest request);

    UserResponse changeStatus(Integer id, UserStatusRequest request);

    void resetPassword(Integer id, ResetPasswordRequest request);

    void deleteUser(Integer id);
}