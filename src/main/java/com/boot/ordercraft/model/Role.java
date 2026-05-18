package com.boot.ordercraft.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ORDER_CRAFT_ROLES")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long role_id;

    @Column(unique = true, nullable = false)
    private String roleName;

    public Role() {}

    public Role(Long role_id, String roleName) {
        this.role_id = role_id;
        this.roleName = roleName;
    }

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "Role [role_id=" + role_id + ", roleName=" + roleName + "]";
    }
}
