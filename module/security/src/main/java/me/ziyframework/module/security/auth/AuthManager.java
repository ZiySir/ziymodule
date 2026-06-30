package me.ziyframework.module.security.auth;

import lombok.RequiredArgsConstructor;
import me.ziyframework.module.webmvc.common.WebHolder;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * 认证管理.
 * created in 2026-05
 * @author ziy
 */
@RequiredArgsConstructor
public class AuthManager {

    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository repository;

    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    /**
     * 登录.
     * <p>通过 {@link AuthenticationManager} 完成认证,把已认证的 Authentication 写入 SecurityContext
     * 并持久化到 Repository.</p>
     * @param username 登录的用户账号.
     */
    public void login(PrincipalType type, String username, String plainPassword) {
        MultiUsernamePasswordAuthenticationToken authentication =
                new MultiUsernamePasswordAuthenticationToken(type, username, plainPassword);
        Authentication authenticated = authenticationManager.authenticate(authentication);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);

        repository.saveContext(context, WebHolder.getRequest(), WebHolder.getResponse());
    }

    /**
     * 登出: 清空 SecurityContext 并 invalidate session.
     */
    public void logout() {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        logoutHandler.logout(WebHolder.getRequest(), WebHolder.getResponse(), current);
    }

    /**
     * 获取当前用户上下文 {@link Authentication}.
     */
    public @Nullable Authentication current() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
