package me.ziyframework.module.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.ziyframework.boot.data.spring.jpa.entity.JpaRelationalBaseEntity;
import org.jspecify.annotations.Nullable;

/**
 * 主体与角色的多态关联.
 * principalType + principalId 用于定位任意账号体系下的主体(如 BackendUser).
 * 业务层应保证 role.ownerType == principalType.
 * created in 2026-06
 * @author ziy
 */
@Entity
@Table(name = "principal_role")
@Data
@EqualsAndHashCode(callSuper = true)
public class PrincipalRoleDo extends JpaRelationalBaseEntity {

    /**
     * 主体类型. 与 Role.ownerType 对应.
     */
    private @Nullable PrincipalType principalType;

    /**
     * 主体 id. 例如 BackendUser.id.
     */
    private @Nullable Long principalId;

    /**
     * 角色 id.
     */
    private @Nullable Long roleId;
}
