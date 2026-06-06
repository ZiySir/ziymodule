package me.ziyframework.module.security.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import me.ziyframework.module.webmvc.common.WebHolder;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
     * @param loginModel 登录主体信息.
     * @param credentialSupplier 凭据提供(Token创建)
     * @param authoritiesSupplier 权限提供
     */
    public void login(
            LoginModel loginModel,
            Supplier<Object> credentialSupplier,
            Supplier<Collection<GrantedAuthority>> authoritiesSupplier) {
        Authentication authentication = new LazyAuthenticationToken(loginModel, null, Collections::emptyList);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        repository.saveContext(context, WebHolder.getRequest(), WebHolder.getResponse());
    }

    /**
     * 登出.
     */
    public void logout() {
        SecurityContextHolder.clearContext();
        repository.saveContext(
                SecurityContextHolder.createEmptyContext(), WebHolder.getRequest(), WebHolder.getResponse());
    }

    /**
     * 获取当前用户上下文 {@link Authentication}.
     */
    public @Nullable Authentication current() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
