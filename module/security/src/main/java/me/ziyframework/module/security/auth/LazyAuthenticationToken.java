package me.ziyframework.module.security.auth;

import com.google.common.base.Preconditions;
import java.util.Collection;
import me.ziyframework.framework.Lazy;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * 支持懒加载权限.
 * created in 2026-05
 * @author ziy
 */
public class LazyAuthenticationToken extends UsernamePasswordAuthenticationToken {

    /**
     * 用于懒加载Authorities数据.
     */
    private final Lazy<Collection<GrantedAuthority>> lazyAuthorities;

    /**
     * 用于 AuthenticationProvider 在认证完成后重建已认证 token.
     */
    private final AuthorityResolver authorityResolver;

    /**
     * 构造支持懒加载的AuthenticationToken.
     * @param principal 认证主体 (LoginModel)
     * @param credentials 认证凭据 (password),已认证场景传 null
     * @param authorityResolver 懒加载权限实现
     */
    public LazyAuthenticationToken(
            LoginModel principal, @Nullable Object credentials, AuthorityResolver authorityResolver) {
        super(principal, credentials);
        Preconditions.checkNotNull(authorityResolver, "authorityResolver is null");
        this.authorityResolver = authorityResolver;
        this.lazyAuthorities = Lazy.of(() -> authorityResolver.resolve(principal));
    }

    /**
     * 获取Authorities.
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return lazyAuthorities.get();
    }

    /**
     * 获取 {@link LoginModel}.
     */
    public LoginModel getLoginModel() {
        return (LoginModel) Preconditions.checkNotNull(getPrincipal(), "principal is null");
    }

    /**
     * 获取 {@link AuthorityResolver},用于 AuthenticationProvider 重建已认证 token.
     */
    public AuthorityResolver getAuthorityResolver() {
        return authorityResolver;
    }
}
