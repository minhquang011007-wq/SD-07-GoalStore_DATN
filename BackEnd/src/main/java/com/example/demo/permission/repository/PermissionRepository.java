package com.example.demo.permission.repository;

import com.example.demo.permission.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Integer> {

    Optional<PermissionEntity> findByCode(String code);

    List<PermissionEntity> findByModuleOrderByCodeAsc(String module);

    List<PermissionEntity> findAllByOrderByModuleAscCodeAsc();
}