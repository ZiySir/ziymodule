package me.ziyframework.module.security.entity;

import java.util.Optional;
import me.ziyframework.boot.data.spring.jpa.repository.SpringDataJpaBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link BackendUserDo}的Repository.
 * created in 2026-06
 * @author ziy
 */
@Repository
public interface BackendUserDoRepository extends SpringDataJpaBaseRepository<BackendUserDo> {

    /**
     * 按用户名查询.
     */
    Optional<BackendUserDo> findByUsername(String username);
}
