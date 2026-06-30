package me.ziyframework.service.user.entity;

import jakarta.persistence.Column;
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

    @Column(length = 64, nullable = false, unique = true, comment = "权限编码,全局唯一. 例如 user:read")
    private @Nullable String code;

    @Column(length = 64, nullable = false, comment = "权限显示名称(display name)")
    private @Nullable String name;

    @Column(nullable = false, options = "default false", comment = "是否内置. true 时禁止删除/改 code")
    private @Nullable Boolean builtin;

    @Column(nullable = false, options = "default false", comment = "是否禁用. true 时权限解析阶段会被过滤掉")
    private @Nullable Boolean disabled;

    @Column(length = 255, comment = "权限备注")
    private @Nullable String remark;
}
