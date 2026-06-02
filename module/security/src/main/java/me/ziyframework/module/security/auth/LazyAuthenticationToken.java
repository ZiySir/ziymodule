package me.ziyframework.module.security.auth;

import java.util.Collection;
import java.util.function.Supplier;
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

    public LazyAuthenticationToken(
            @Nullable Object principal,
            @Nullable Object credentials,
            Supplier<Collection<GrantedAuthority>> lazyAuthorities) {
        super(principal, credentials);
        this.lazyAuthorities = Lazy.of(lazyAuthorities);
    }

    /**
     * 获取Authorities.
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return lazyAuthorities.get();
    }
}
