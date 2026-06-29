package me.ziyframework.example.auth;

import lombok.RequiredArgsConstructor;
import me.ziyframework.module.security.auth.MultiUsernamePasswordAuthenticationToken;
import me.ziyframework.module.security.utils.Securitys;
import me.ziyframework.module.webmvc.common.dto.Result;
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
    public Result<MultiUsernamePasswordAuthenticationToken> login(@RequestBody LoginRequest req) {
        userService.login(req.username(), req.password());
        MultiUsernamePasswordAuthenticationToken current = Securitys.getCurrentOrThrow();
        return Result.ok(current);
    }

    /** 登出. */
    @PostMapping("/logout")
    public Result<Void> logout() {
        Securitys.logout();
        return Result.ok();
    }

    /** 登录请求体. */
    public record LoginRequest(String username, String password) {}
}
