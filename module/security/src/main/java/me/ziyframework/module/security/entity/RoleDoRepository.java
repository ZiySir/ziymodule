package me.ziyframework.module.security.entity;

import java.util.Set;
import me.ziyframework.boot.data.spring.jpa.repository.SpringDataJpaBaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * {@link RoleDo}的Repository.
 * created in 2026-06
 * @author ziy
 */
@Repository
public interface RoleDoRepository extends SpringDataJpaBaseRepository<RoleDo> {

    /**
     * 单条 SQL 取主体的全部角色 id(含沿 {@code parent_id} 上行的所有祖先).
     */
    @Query(value = """
        WITH RECURSIVE role_tree AS (
            SELECT r.id, r.parent_id
            FROM role r
            JOIN principal_role pr ON pr.role_id = r.id
            WHERE pr.principal_type = :type
              AND pr.principal_id = :principalId
              AND r.disabled = false
            UNION ALL
            SELECT r.id, r.parent_id
            FROM role r
            INNER JOIN role_tree rt ON rt.parent_id = r.id
        )
        SELECT DISTINCT rt.id FROM role_tree rt
        """, nativeQuery = true)
    Set<Long> getAllEnabledRoleIdByPrincipalIdAndType(@Param("type") int type, @Param("principalId") Long principalId);
}
