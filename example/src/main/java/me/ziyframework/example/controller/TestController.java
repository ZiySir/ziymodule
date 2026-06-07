package me.ziyframework.example.controller;

import me.ziyframework.module.security.auth.LazyAuthenticationToken;
import me.ziyframework.module.security.auth.LoginModel;
import me.ziyframework.module.security.utils.Securitys;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 * created in 2026-05
 * @author ziy
 */
@SuppressWarnings("checkstyle:DesignForExtension")
@RestController
public class TestController {

    @RequestMapping("/login")
    public String login() {
        return "login success";
    }

    @GetMapping("/get")
    public LoginModel home() {
        LazyAuthenticationToken current = Securitys.getCurrentOrThrow();
        return current.getLoginModel();
    }
}
