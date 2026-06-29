package me.ziyframework.module.security.auth;

import java.util.Collection;
import lombok.Getter;
import me.ziyframework.module.security.entity.PrincipalType;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * 支持多账号体系.
 * created in 2026-05
 * @author ziy
 */
public class MultiUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    /**
     * 多账号类型区分.
     */
    @Getter
    private final PrincipalType type;

    public MultiUsernamePasswordAuthenticationToken(
            PrincipalType principalType, @Nullable Object principal, @Nullable Object credentials) {
        super(principal, credentials);
        this.type = principalType;
    }

    public MultiUsernamePasswordAuthenticationToken(
            PrincipalType principalType,
            Object principal,
            @Nullable Object credentials,
            Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.type = principalType;
    }

    /**
     * 获取Details.
     */
    @Override
    public @Nullable Object getDetails() {
        return super.getDetails();
    }
}
