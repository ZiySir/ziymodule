package me.ziyframework.module.security.utils;

import com.google.common.base.Preconditions;
import me.ziyframework.boot.core.SpringHolder;
import me.ziyframework.module.security.auth.AuthManager;
import me.ziyframework.module.security.auth.MultiUsernamePasswordAuthenticationToken;
import me.ziyframework.module.security.auth.PrincipalType;
import org.springframework.security.core.Authentication;

/**
 * 安全工具类.
 * created in 2026-05
 * @author ziy
 */
public final class Securitys {

    private Securitys() {}

    /**
     * 账号登录.
     */
    public static void login(PrincipalType type, String username, String plainPassword) {
        SpringHolder.getBean(AuthManager.class).login(type, username, plainPassword);
    }

    /**
     * 登出当前用户.
     */
    public static void logout() {
        SpringHolder.getBean(AuthManager.class).logout();
    }

    /**
     * 获取当前上下文，如果不存在则抛出异常.
     */
    public static MultiUsernamePasswordAuthenticationToken getCurrentOrThrow() {
        Authentication current = SpringHolder.getBean(AuthManager.class).current();
        return Preconditions.checkNotNull(
                (MultiUsernamePasswordAuthenticationToken) current, "current user context is null");
    }
}
