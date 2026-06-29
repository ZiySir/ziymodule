package me.ziyframework.module.security.auth;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 登录认证核心逻辑 Provider.
 * created in 2026-06
 *
 * @author ziy
 */
@RequiredArgsConstructor
public abstract class AbstractLoginAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {


    /**
     * {@inheritDoc}
     */
    @Override
    protected abstract void additionalAuthenticationChecks(
            UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException;

    /**
     * {@inheritDoc}
     * 从数据库中检索用户.
     */
    @Override
    protected abstract UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException;

    /**
     * 保持自定义的Authentication相同类型的返回.
     */
    @Override
    protected Authentication createSuccessAuthentication(
            Object principal, Authentication authentication, UserDetails user) {
        Authentication parentResult = super.createSuccessAuthentication(principal, authentication, user);
        // 将返回的Authentication转换到Multi
        MultiUsernamePasswordAuthenticationToken multiAuthentication =
                (MultiUsernamePasswordAuthenticationToken) authentication;

        MultiUsernamePasswordAuthenticationToken authenticated = new MultiUsernamePasswordAuthenticationToken(
                multiAuthentication.getType(),
                Preconditions.checkNotNull(multiAuthentication.getPrincipal(), "principal must not be null"),
                multiAuthentication.getCredentials(), // 使用令牌表示凭证.
                parentResult.getAuthorities());
        authenticated.setDetails(user);
        return authenticated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return MultiUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
