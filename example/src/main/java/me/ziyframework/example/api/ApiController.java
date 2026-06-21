package me.ziyframework.example.api;

import java.util.Map;
import me.ziyframework.module.webmvc.common.dto.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 开放平台 API 端点（受 AK/SK 签名校验保护）. */
@RestController
@RequestMapping("/api")
public class ApiController {

    /** 测试回显. */
    @GetMapping("/echo")
    public Result<String> echo(@RequestParam String msg) {
        return Result.ok("echo: " + msg);
    }

    /** 测试提交. */
    @PostMapping("/submit")
    public Result<Map<String, String>> submit(@RequestBody Map<String, String> body) {
        return Result.ok(Map.of("echo", body.getOrDefault("data", "")));
    }
}
