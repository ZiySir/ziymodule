package me.ziyframework.module.security.auth;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import me.ziyframework.module.security.entity.BackendUserDo;
import me.ziyframework.module.security.entity.BackendUserDoRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 登录认证 Provider.
 * <p>从 {@link LazyAuthenticationToken#getLoginModel()} 读取 username,
 * 按账号查找 {@link BackendUserDo},校验密码与禁用状态,
 * 返回已认证的 {@link LazyAuthenticationToken}.</p>
 * created in 2026-06
 * @author ziy
 */
@Component
@RequiredArgsConstructor
public class LoginAuthenticationProvider implements AuthenticationProvider {

    private final BackendUserDoRepository repository;

    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LazyAuthenticationToken token = (LazyAuthenticationToken) authentication;
        LoginModel loginModel = token.getLoginModel();
        String username = loginModel.username();

        BackendUserDo user;
        try {
            user = repository.findByAccount(username).orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));
        } catch (BadCredentialsException ex) {
            // 保留 BadCredentialsException 不被包装,直接透传给调用方
            throw ex;
        } catch (RuntimeException ex) {
            throw new InternalAuthenticationServiceException("登录失败", ex);
        }

        if (Boolean.TRUE.equals(user.getDisabled())) {
            throw new DisabledException("账号已禁用");
        }

        String raw = Objects.toString(token.getCredentials(), null);
        if (raw == null || !passwordEncoder.matches(raw, user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        return new LazyAuthenticationToken(loginModel, null, token.getAuthorityResolver());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return LazyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
