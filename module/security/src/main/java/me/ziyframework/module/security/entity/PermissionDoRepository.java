package me.ziyframework.module.security.entity;

import java.util.Collection;
import java.util.List;
import me.ziyframework.boot.data.spring.jpa.repository.SpringDataJpaBaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * {@link PermissionDo}的Repository.
 * created in 2026-06
 * @author ziy
 */
@Repository
public interface PermissionDoRepository extends SpringDataJpaBaseRepository<PermissionDo> {

    /**
     * 取角色集合关联的、双方均启用的权限 code.
     * 角色自身的 disabled 在此处一并过滤,以实现"禁用节点不贡献权限"语义.
     */
    @Query("""
            select distinct PermissionDo.code from PermissionDo
                     join RolePermissionDo on RolePermissionDo.permissionId = PermissionDo.id and RolePermissionDo.roleId in :roleIds and PermissionDo.disabled = false
        """)
    List<String> getEnabledCodeByRoleIdIn(@Param("roleIds") Collection<Long> roleIds);
}
