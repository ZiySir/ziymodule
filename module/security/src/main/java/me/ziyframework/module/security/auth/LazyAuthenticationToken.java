package me.ziyframework.module.security.auth;

import com.google.common.base.Preconditions;
import java.util.Collection;
import me.ziyframework.framework.Lazy;
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
     * 构造支持懒加载的AuthenticationToken.
     * @param principal 认证主体 (username)
     * @param credentials 认证凭据 (password)
     * @param authorityResolver 懒加载权限实现
     */
    public LazyAuthenticationToken(LoginModel principal, Object credentials, AuthorityResolver authorityResolver) {
        super(principal, credentials);
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
}
