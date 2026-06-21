package me.ziyframework.example.auth;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import me.ziyframework.module.security.auth.LoginModel;
import me.ziyframework.module.security.entity.BackendUserDo;
import me.ziyframework.module.security.entity.BackendUserDoRepository;
import me.ziyframework.module.security.entity.PrincipalType;
import me.ziyframework.module.security.utils.Securitys;
import org.springframework.stereotype.Service;

/** 后台用户登录服务（明文密码校验，演示用）. */
@Service
@RequiredArgsConstructor
public class BackendUserService {

    private final BackendUserDoRepository repository;

    /**
     * 校验密码并写入 session.
     * @throws IllegalArgumentException 用户不存在、密码错误或账号被禁用
     */
    public LoginModel login(String username, String password) {
        BackendUserDo user =
                repository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
        if (!Objects.equals(user.getPassword(), password)) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (Boolean.TRUE.equals(user.getDisabled())) {
            throw new IllegalArgumentException("账号已禁用");
        }
        LoginModel model = new LoginModel(PrincipalType.BACKEND, user.getIdOrThrow(), null);
        Securitys.login(model, password);
        return model;
    }
}
