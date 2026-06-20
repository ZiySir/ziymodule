package me.ziyframework.module.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.ziyframework.boot.data.spring.jpa.entity.JpaRelationalBaseEntity;
import org.jspecify.annotations.Nullable;

/**
 * 角色.
 * 自引用 parentId 形成角色树,权限解析时上行继承父角色权限.
 * 通过 ownerType 区分多账号体系.
 * created in 2026-06
 * @author ziy
 */
@Entity
@Table(name = "role")
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleDo extends JpaRelationalBaseEntity {

    /**
     * 角色编码,全局唯一,代码/配置引用使用.
     */
    private @Nullable String code;

    /**
     * 显示名.
     */
    private @Nullable String name;

    /**
     * 父角色 id. null 表示根角色. 权限上行继承.
     */
    private @Nullable Long parentId;

    /**
     * 该角色所属的账号体系. 与 PrincipalRole.principalType 对应.
     */
    private @Nullable PrincipalType ownerType;

    /**
     * 排序权重,后台展示用.
     */
    private @Nullable Integer sort;

    /**
     * 是否内置. true 时禁止删除/改 code,由 Service 层强制.
     */
    private @Nullable Boolean builtin;

    /**
     * 是否禁用. 禁用时本节点不参与权限解析,但继承链不截断 (跳过本节点继续向上).
     */
    private @Nullable Boolean disabled;

    /**
     * 备注.
     */
    private @Nullable String remark;
}
