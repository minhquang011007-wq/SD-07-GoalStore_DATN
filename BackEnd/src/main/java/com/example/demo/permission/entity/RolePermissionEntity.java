package com.example.demo.permission.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "Role_Permissions",
        uniqueConstraints = @UniqueConstraint(name = "UQ_RolePermissions", columnNames = {"role", "permission_id"})
)
public class RolePermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private PermissionEntity permission;

    public RolePermissionEntity() {
    }

    public Integer getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public PermissionEntity getPermission() {
        return permission;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPermission(PermissionEntity permission) {
        this.permission = permission;
    }
}