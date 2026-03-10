package com.example.demo.permission.repository;

import com.example.demo.permission.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, Integer> {

    List<RolePermissionEntity> findByRole(String role);

    Optional<RolePermissionEntity> findByRoleAndPermission_Code(String role, String code);

    boolean existsByRoleAndPermission_Code(String role, String code);

    void deleteByRoleAndPermission_Code(String role, String code);

    void deleteByRole(String role);
}