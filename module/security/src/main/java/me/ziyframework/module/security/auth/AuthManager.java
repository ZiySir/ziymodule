package me.ziyframework.module.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * 认证管理.
 * created in 2026-05
 * @author ziy
 */
public class AuthManager {

    private final SecurityContextRepository repository;

    public AuthManager(SecurityContextRepository repository) {
        this.repository = repository;
    }

    /**
     * 登录.
     */
    public void login(HttpServletRequest request, HttpServletResponse response, LoginModel loginModel) {
        Authentication authentication = new LazyAuthenticationToken(loginModel, null, Collections::emptyList);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        repository.saveContext(context, request, response);
    }

    /**
     * 登出.
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        repository.saveContext(SecurityContextHolder.createEmptyContext(), request, response);
    }
}
