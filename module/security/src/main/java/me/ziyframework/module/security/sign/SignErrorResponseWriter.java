package me.ziyframework.module.security.sign;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import me.ziyframework.module.webmvc.common.dto.Result;
import me.ziyframework.module.webmvc.common.dto.ResultCode;
import org.springframework.http.MediaType;
import tools.jackson.databind.json.JsonMapper;

/**
 * 签名校验失败的 JSON 响应写入器.
 */
@RequiredArgsConstructor
public class SignErrorResponseWriter {

    private final JsonMapper jsonMapper;

    /**
     * 将错误码对应的 JSON 响应写入 servlet 输出.
     */
    public void write(HttpServletResponse response, ResultCode code) throws IOException {
        response.setStatus(code.code() / 100);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter().write(jsonMapper.writeValueAsString(Result.of(code)));
    }
}
