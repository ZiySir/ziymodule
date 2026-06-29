package me.ziyframework.module.security.auth;

import java.util.Collection;
import me.ziyframework.module.security.entity.PrincipalType;
import org.springframework.security.core.GrantedAuthority;

/**
 * 权限构建接口.
 * created in 2026-06
 * @author ziy
 */
@FunctionalInterface
public interface AuthorityResolver {

    /**
     * 根据主体信息获取权限.
     */
    Collection<GrantedAuthority> resolve(PrincipalType type, long userId);
}
