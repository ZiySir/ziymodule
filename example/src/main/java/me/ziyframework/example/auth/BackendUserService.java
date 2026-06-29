package me.ziyframework.example.auth;

import lombok.RequiredArgsConstructor;
import me.ziyframework.module.security.entity.BackendUserDoRepository;
import me.ziyframework.module.security.entity.PrincipalType;
import me.ziyframework.module.security.utils.Securitys;
import org.springframework.stereotype.Service;

/** 后台用户登录服务（账号/密码/禁用校验由 LoginAuthenticationProvider 完成）. */
@Service
@RequiredArgsConstructor
public class BackendUserService {

    private final BackendUserDoRepository repository;

    /**
     * 组装登录主体并写入 session.
     * <p>账号存在性、密码正确性、禁用状态由 LoginAuthenticationProvider 校验;
     * 失败时 Provider 抛 {@link org.springframework.security.authentication.BadCredentialsException}
     * 或 {@link org.springframework.security.authentication.DisabledException},由调用方处理.</p>
     */
    public void login(String username, String password) {
        Securitys.login(PrincipalType.BACKEND, username, password);
    }
}
