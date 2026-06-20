package me.ziyframework.module.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.ziyframework.boot.data.spring.jpa.entity.JpaRelationalBaseEntity;
import org.jspecify.annotations.Nullable;

/**
 * 角色与权限的关联.
 * created in 2026-06
 * @author ziy
 */
@Entity
@Table(name = "role_permission")
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePermissionDo extends JpaRelationalBaseEntity {

    /**
     * 角色 id.
     */
    private @Nullable Long roleId;

    /**
     * 权限 id.
     */
    private @Nullable Long permissionId;
}
