package com.example.demo.user.repository;

import com.example.demo.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface URepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    Page<UserEntity> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String email, Pageable pageable
    );

    Page<UserEntity> findByRole(String role, Pageable pageable);

    Page<UserEntity> findByTrangThai(String trangThai, Pageable pageable);

    Page<UserEntity> findByRoleAndTrangThai(String role, String trangThai, Pageable pageable);
}