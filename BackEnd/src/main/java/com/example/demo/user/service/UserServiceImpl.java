package com.example.demo.user.service;

import com.example.demo.audit.service.AuditLogService;
import com.example.demo.user.dto.request.ResetPasswordRequest;
import com.example.demo.user.dto.request.UserCreateRequest;
import com.example.demo.user.dto.request.UserStatusRequest;
import com.example.demo.user.dto.request.UserUpdateRequest;
import com.example.demo.user.dto.response.UserResponse;
import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.exception.BadRequestException;
import com.example.demo.user.exception.ResourceNotFoundException;
import com.example.demo.user.repository.URepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private URepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public Page<UserResponse> getAllUsers(String q, String role, String trangThai, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<UserEntity> userPage;

        if (q != null && !q.trim().isEmpty()) {
            userPage = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, pageable);
        } else if (role != null && !role.isEmpty() && trangThai != null && !trangThai.isEmpty()) {
            userPage = userRepository.findByRoleAndTrangThai(role, trangThai, pageable);
        } else if (role != null && !role.isEmpty()) {
            userPage = userRepository.findByRole(role, pageable);
        } else if (trangThai != null && !trangThai.isEmpty()) {
            userPage = userRepository.findByTrangThai(trangThai, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return userPage.map(this::mapToResponse);
    }

    @Override
    public UserResponse getUserById(Integer id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id = " + id));
        return mapToResponse(user);
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username đã tồn tại");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã tồn tại");
        }

        validateRole(request.getRole());
        validateStatus(request.getTrangThai());

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole().trim().toUpperCase());
        user.setTrangThai(request.getTrangThai().trim().toUpperCase());

        UserEntity savedUser = userRepository.save(user);

        auditLogService.log(
                getCurrentUserId(),
                "CREATE_USER",
                "USER",
                savedUser.getId(),
                "Tạo user mới: username=" + savedUser.getUsername() + ", role=" + savedUser.getRole()
        );

        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Integer id, UserUpdateRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id = " + id));

        String oldEmail = user.getEmail();
        String oldRole = user.getRole();
        String oldTrangThai = user.getTrangThai();

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new BadRequestException("Email đã tồn tại");
            }
            user.setEmail(request.getEmail().trim());
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            validateRole(request.getRole());
            user.setRole(request.getRole().trim().toUpperCase());
        }

        if (request.getTrangThai() != null && !request.getTrangThai().isBlank()) {
            validateStatus(request.getTrangThai());
            user.setTrangThai(request.getTrangThai().trim().toUpperCase());
        }

        UserEntity savedUser = userRepository.save(user);

        String detail = "Cập nhật user id=" + savedUser.getId()
                + " | email: " + oldEmail + " -> " + savedUser.getEmail()
                + " | role: " + oldRole + " -> " + savedUser.getRole()
                + " | status: " + oldTrangThai + " -> " + savedUser.getTrangThai();

        auditLogService.log(
                getCurrentUserId(),
                "UPDATE_USER",
                "USER",
                savedUser.getId(),
                detail
        );

        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse changeStatus(Integer id, UserStatusRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id = " + id));

        String oldStatus = user.getTrangThai();

        validateStatus(request.getTrangThai());
        user.setTrangThai(request.getTrangThai().trim().toUpperCase());

        UserEntity savedUser = userRepository.save(user);

        auditLogService.log(
                getCurrentUserId(),
                "CHANGE_USER_STATUS",
                "USER",
                savedUser.getId(),
                "Đổi trạng thái user " + savedUser.getUsername() + ": " + oldStatus + " -> " + savedUser.getTrangThai()
        );

        return mapToResponse(savedUser);
    }

    @Override
    public void resetPassword(Integer id, ResetPasswordRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id = " + id));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditLogService.log(
                getCurrentUserId(),
                "RESET_PASSWORD",
                "USER",
                user.getId(),
                "Reset mật khẩu cho user: " + user.getUsername()
        );
    }

    @Override
    public void deleteUser(Integer id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id = " + id));

        String username = user.getUsername();

        auditLogService.log(
                getCurrentUserId(),
                "DELETE_USER",
                "USER",
                user.getId(),
                "Xóa user: username=" + username + ", role=" + user.getRole()
        );

        userRepository.delete(user);
    }

    private UserResponse mapToResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getTrangThai(),
                user.getCreatedAt()
        );
    }

    private void validateRole(String role) {
        String value = role.trim().toUpperCase();
        if (!value.equals("ADMIN") && !value.equals("SALES") && !value.equals("INVENTORY")) {
            throw new BadRequestException("Role không hợp lệ");
        }
    }

    private void validateStatus(String status) {
        String value = status.trim().toUpperCase();
        if (!value.equals("ACTIVE") && !value.equals("INACTIVE") && !value.equals("LOCKED")) {
            throw new BadRequestException("Trạng thái không hợp lệ");
        }
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        if (username == null || username.equals("anonymousUser")) {
            return null;
        }

        return userRepository.findByUsername(username)
                .map(UserEntity::getId)
                .orElse(null);
    }
}