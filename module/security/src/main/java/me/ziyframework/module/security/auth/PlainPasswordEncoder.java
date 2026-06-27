package me.ziyframework.module.security.auth;

import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 明文密码编码器(仅供示例/迁移过渡使用).
 * <p>生产环境应替换为 BCryptPasswordEncoder. 此实现保持与原 BackendUserService 中
 * {@code Objects.equals(password, rawPassword)} 一致的行为.</p>
 * created in 2026-06
 * @author ziy
 */
public class PlainPasswordEncoder implements PasswordEncoder {

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(@Nullable CharSequence rawPassword) {
        return rawPassword == null ? "" : rawPassword.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@Nullable CharSequence rawPassword, @Nullable String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return Objects.equals(rawPassword.toString(), encodedPassword);
    }
}
