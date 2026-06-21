package me.ziyframework.example.auth;

import lombok.RequiredArgsConstructor;
import me.ziyframework.module.security.auth.LoginModel;
import me.ziyframework.module.security.utils.Securitys;
import me.ziyframework.module.webmvc.common.dto.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** 账号认证相关端点. */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final BackendUserService userService;

    /** 登录. */
    @PostMapping("/login")
    public Result<LoginModel> login(@RequestBody LoginRequest req) {
        return Result.ok(userService.login(req.username(), req.password()));
    }

    /** 登出. */
    @PostMapping("/logout")
    public Result<Void> logout() {
        Securitys.logout();
        return Result.ok();
    }

    /** 读取当前登录主体. */
    @GetMapping("/whoami")
    public Result<LoginModel> whoami() {
        return Result.ok(Securitys.getCurrentOrThrow().getLoginModel());
    }

    /** 登录请求体. */
    public record LoginRequest(String username, String password) {}
}
