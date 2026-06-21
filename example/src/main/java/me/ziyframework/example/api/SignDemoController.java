package me.ziyframework.example.api;

import java.util.Map;
import java.util.Objects;
import me.ziyframework.module.webmvc.common.dto.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

/** 签名流程自检：用 {@link SignClient} 调自身 {@code /api/echo} 验证完整链路. */
@RestController
public class SignDemoController {

    private final RestClient restClient = RestClient.create("http://localhost:8080");

    /** 触发自检. */
    @GetMapping("/sign-self-test")
    public Result<String> selfTest() {
        Map<String, String> headers = SignClient.signHeaders(
                "ak-demo-001",
                "sk-demo-001-secret",
                "GET",
                "/api/echo",
                Map.of("msg", new String[] {"hi"}),
                new byte[0]);
        String response = restClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path("/api/echo").queryParam("msg", "hi").build())
                .headers(h -> h.setAll(headers))
                .retrieve()
                .body(String.class);
        return Result.ok(Objects.requireNonNullElse(response, ""));
    }
}
