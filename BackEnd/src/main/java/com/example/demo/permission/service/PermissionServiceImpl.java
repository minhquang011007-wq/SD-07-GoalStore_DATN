package com.example.demo.permission.service;

import com.example.demo.audit.service.AuditLogService;

import com.example.demo.permission.dto.request.RolePermissionUpdateRequest;
import com.example.demo.permission.dto.response.PermissionResponse;
import com.example.demo.permission.entity.PermissionEntity;
import com.example.demo.permission.entity.RolePermissionEntity;
import com.example.demo.permission.repository.PermissionRepository;
import com.example.demo.permission.repository.RolePermissionRepository;
import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.exception.BadRequestException;
import com.example.demo.user.repository.URepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private URepository userRepository;

    @Override
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAllByOrderByModuleAscCodeAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PermissionResponse> getPermissionsByModule(String module) {
        if (module == null || module.isBlank()) {
            throw new BadRequestException("Module không được để trống");
        }

        return permissionRepository.findByModuleOrderByCodeAsc(module.trim().toUpperCase())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PermissionResponse> getPermissionsByRole(String role) {
        String normalizedRole = normalizeRole(role);

        return rolePermissionRepository.findByRole(normalizedRole)
                .stream()
                .map(rolePermission -> mapToResponse(rolePermission.getPermission()))
                .sorted(Comparator.comparing(PermissionResponse::getModule)
                        .thenComparing(PermissionResponse::getCode))
                .toList();
    }

    @Override
    public Map<String, Object> updateRolePermissions(RolePermissionUpdateRequest request) {
        String normalizedRole = normalizeRole(request.getRole());

        Set<String> requestedCodes = request.getPermissionCodes().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (requestedCodes.isEmpty()) {
            throw new BadRequestException("Danh sách permission không được để trống");
        }

        List<PermissionEntity> permissions = new ArrayList<>();
        for (String code : requestedCodes) {
            PermissionEntity permission = permissionRepository.findByCode(code)
                    .orElseThrow(() -> new BadRequestException("Permission không tồn tại: " + code));
            permissions.add(permission);
        }

        rolePermissionRepository.deleteByRole(normalizedRole);

        for (PermissionEntity permission : permissions) {
            RolePermissionEntity rolePermission = new RolePermissionEntity();
            rolePermission.setRole(normalizedRole);
            rolePermission.setPermission(permission);
            rolePermissionRepository.save(rolePermission);
        }

        auditLogService.log(
                getCurrentUserId(),
                "UPDATE_ROLE_PERMISSION",
                "ROLE_PERMISSION",
                null,
                "Cập nhật quyền cho role=" + normalizedRole + " | permissions=" + requestedCodes
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("role", normalizedRole);
        result.put("permissions", getPermissionsByRole(normalizedRole));
        result.put("message", "Cập nhật quyền thành công");
        return result;
    }

    @Override
    public Map<String, Object> grantPermission(String role, String permissionCode) {
        String normalizedRole = normalizeRole(role);
        String normalizedCode = normalizeCode(permissionCode);

        PermissionEntity permission = permissionRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new BadRequestException("Permission không tồn tại: " + normalizedCode));

        if (rolePermissionRepository.existsByRoleAndPermission_Code(normalizedRole, normalizedCode)) {
            throw new BadRequestException("Role đã có permission này");
        }

        RolePermissionEntity rolePermission = new RolePermissionEntity();
        rolePermission.setRole(normalizedRole);
        rolePermission.setPermission(permission);
        rolePermissionRepository.save(rolePermission);

        auditLogService.log(
                getCurrentUserId(),
                "GRANT_PERMISSION",
                "ROLE_PERMISSION",
                null,
                "Gán quyền " + normalizedCode + " cho role=" + normalizedRole
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("role", normalizedRole);
        result.put("permission", normalizedCode);
        result.put("message", "Gán quyền thành công");
        return result;
    }

    @Override
    public Map<String, Object> revokePermission(String role, String permissionCode) {
        String normalizedRole = normalizeRole(role);
        String normalizedCode = normalizeCode(permissionCode);

        if (!rolePermissionRepository.existsByRoleAndPermission_Code(normalizedRole, normalizedCode)) {
            throw new BadRequestException("Role chưa có permission này");
        }

        rolePermissionRepository.deleteByRoleAndPermission_Code(normalizedRole, normalizedCode);

        auditLogService.log(
                getCurrentUserId(),
                "REVOKE_PERMISSION",
                "ROLE_PERMISSION",
                null,
                "Thu hồi quyền " + normalizedCode + " khỏi role=" + normalizedRole
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("role", normalizedRole);
        result.put("permission", normalizedCode);
        result.put("message", "Thu hồi quyền thành công");
        return result;
    }

    private PermissionResponse mapToResponse(PermissionEntity entity) {
        return new PermissionResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getModule()
        );
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            throw new BadRequestException("Role không được để trống");
        }

        String value = role.trim().toUpperCase();
        if (!value.equals("ADMIN") && !value.equals("SALES") && !value.equals("INVENTORY")) {
            throw new BadRequestException("Role không hợp lệ");
        }
        return value;
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new BadRequestException("Permission code không được để trống");
        }
        return code.trim().toUpperCase();
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