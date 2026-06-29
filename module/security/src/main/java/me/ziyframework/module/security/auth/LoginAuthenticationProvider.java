package me.ziyframework.module.security.auth;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import me.ziyframework.module.security.auth.detail.AuthUserDetails;
import me.ziyframework.module.security.entity.BackendUserDoRepository;
import me.ziyframework.module.security.entity.RoleDoRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 登录认证核心逻辑 Provider.
 * created in 2026-06
 * @author ziy
 */
@Component
@RequiredArgsConstructor
public class LoginAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final BackendUserDoRepository backendUserDoRepository;

    private final RoleDoRepository roleDoRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        // 暂时
    }

    /**
     * {@inheritDoc}
     * 从数据库中检索用户.
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        return backendUserDoRepository
                .findByUsername(username)
                .map(backendUserDo -> {
                    new AuthUserDetails(backendUserDo.getUid(), backendUserDo.getUsername(), )
                })
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

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
