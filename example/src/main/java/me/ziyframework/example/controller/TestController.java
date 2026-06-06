package me.ziyframework.example.controller;

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

    @GetMapping("/test/home")
    public String home() {
        return "home测试返回成功";
    }
}
