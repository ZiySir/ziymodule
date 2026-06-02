package me.ziyframework.module.security.utils;

import me.ziyframework.boot.core.SpringHolder;
import me.ziyframework.framework.Lazy;
import me.ziyframework.module.security.auth.AuthManager;
import me.ziyframework.module.security.auth.LoginModel;
import me.ziyframework.module.webmvc.common.WebHolder;

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
     * @param type 登录类型
     * @param id 登录唯一标识
     * @param extra 扩展数据
     */
    public static void login(String type, Object id, Object extra) {
        final AuthManager authManager = AUTH_MANAGER_LAZY.get();
        authManager.login(WebHolder.getRequest(), WebHolder.getResponse(), new LoginModel(type, id, extra));
    }

    /**
     * 登出当前用户.
     */
    public static void logout() {
        final AuthManager authManager = AUTH_MANAGER_LAZY.get();
        authManager.logout(WebHolder.getRequest(), WebHolder.getResponse());
    }
}
