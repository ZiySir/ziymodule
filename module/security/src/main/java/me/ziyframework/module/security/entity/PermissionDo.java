package me.ziyframework.module.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.ziyframework.boot.data.spring.jpa.entity.JpaRelationalBaseEntity;
import org.jspecify.annotations.Nullable;

/**
 * 权限.
 * 字符串 code 对应 Spring Security GrantedAuthority#getAuthority().
 * 不区分账号体系——多账号体系隔离由 Role.ownerType + PrincipalRole.principalType 表达.
 * created in 2026-06
 * @author ziy
 */
@Entity
@Table(name = "permission")
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionDo extends JpaRelationalBaseEntity {

    /**
     * 权限编码,全局唯一. 例如 "user:read".
     */
    private @Nullable String code;

    /**
     * 显示名.
     */
    private @Nullable String name;

    /**
     * 是否内置. true 时禁止删除/改 code,由 Service 层强制.
     */
    private @Nullable Boolean builtin;

    /**
     * 是否禁用. true 时权限解析阶段会被过滤掉.
     */
    private @Nullable Boolean disabled;

    /**
     * 备注.
     */
    private @Nullable String remark;
}
