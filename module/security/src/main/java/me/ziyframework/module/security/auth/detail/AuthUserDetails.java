package me.ziyframework.module.security.auth.detail;

import java.util.Collection;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户UserDetails实现.
 */
public record AuthUserDetails(String uid, String username, Collection<GrantedAuthority> authorities, boolean disabled, boolean locked)
        implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return null;
    }

    /**
     * 返回username.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 账号是否启用.
     */
    @Override
    public boolean isEnabled() {
        return !disabled;
    }

    /**
     * 账号是否锁定.
     */
    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }
}
