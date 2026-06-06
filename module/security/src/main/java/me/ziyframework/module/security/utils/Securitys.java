package me.ziyframework.module.security.utils;

import com.google.common.base.Preconditions;
import me.ziyframework.boot.core.SpringHolder;
import me.ziyframework.framework.Lazy;
import me.ziyframework.module.security.auth.AuthManager;
import me.ziyframework.module.security.auth.LoginModel;
import org.springframework.security.core.Authentication;

/**
 * 安全工具类.
 * created in 2026-05
 * @author ziy
 */
public final class Securitys {

    private static final Lazy<AuthManager> AUTH_MANAGER_LAZY = Lazy.of(() -> SpringHolder.getBean(AuthManager.class));

    private Securitys() {}

    /**
     * 账号登录.
     */
    public static void login(LoginModel loginModel) {
        final AuthManager authManager = AUTH_MANAGER_LAZY.get();
        //        authManager.login(loginModel);
    }

    /**
     * 登出当前用户.
     */
    public static void logout() {
        final AuthManager authManager = AUTH_MANAGER_LAZY.get();
        authManager.logout();
    }

    /**
     * 获取当前上下文，如果不存在则抛出异常.
     */
    public static Authentication getCurrentOrThrow() {
        Authentication current = AUTH_MANAGER_LAZY.get().current();
        return Preconditions.checkNotNull(current, "current user context is null");
    }
}
