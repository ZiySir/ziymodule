package me.ziyframework.module.security.sign;

import com.google.common.hash.Hashing;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import me.ziyframework.module.security.sign.exception.SignValidationException;
import me.ziyframework.module.security.sign.nonce.NonceGuard;
import me.ziyframework.module.security.sign.provider.CallerSecretProvider;
import me.ziyframework.module.webmvc.common.dto.ResultCode;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

/** AK/SK 签名校验过滤器. */
@Slf4j
public class SignValidatorServletFilter implements Filter {

    private static final String HEADER_AK = "X-Sign-AK";
    private static final String HEADER_SIGNATURE = "X-Sign-Signature";
    private static final String HEADER_TIMESTAMP = "X-Sign-Timestamp";
    private static final String HEADER_NONCE = "X-Sign-Nonce";

    /**
     * 时间戳容差(秒).
     */
    private final long timestampToleranceSeconds;

    /**
     * nonce的时间(秒)
     */
    private final long nonceTtlSeconds;

    /**
     * 最大的RequestBody字节.
     */
    private final int maxBodyBytes;

    /**
     * 标准请求构建器.
     */
    private final CanonicalRequestBuilder canonicalBuilder;

    /**
     * SK的Provider.
     */
    private final CallerSecretProvider secretProvider;

    /**
     * nonce重放守卫.
     */
    private final NonceGuard nonceGuard;

    /**
     * 用于写入错误处理时的JSON.
     */
    private final SignErrorResponseWriter errorWriter;

    public SignValidatorServletFilter(
            long timestampToleranceSeconds,
            long nonceTtlSeconds,
            int maxBodyBytes,
            CanonicalRequestBuilder canonicalBuilder,
            CallerSecretProvider secretProvider,
            NonceGuard nonceGuard,
            SignErrorResponseWriter errorWriter) {
        this.timestampToleranceSeconds = timestampToleranceSeconds;
        this.nonceTtlSeconds = nonceTtlSeconds;
        this.maxBodyBytes = maxBodyBytes;
        this.canonicalBuilder = canonicalBuilder;
        this.secretProvider = secretProvider;
        this.nonceGuard = nonceGuard;
        this.errorWriter = errorWriter;
    }

    /** 过滤器主入口. */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        try {
            doSignCheck(req);
        } catch (SignValidationException ex) {
            errorWriter.write(res, ex.getCode());
            return;
        }
        chain.doFilter(req, res);
    }

    private void doSignCheck(HttpServletRequest req) throws IOException {
        String ak = req.getHeader(HEADER_AK);
        String signature = req.getHeader(HEADER_SIGNATURE);
        String timestamp = req.getHeader(HEADER_TIMESTAMP);
        String nonce = req.getHeader(HEADER_NONCE);

        if (StringUtils.hasText(ak)
                || StringUtils.hasText(signature)
                || StringUtils.hasText(timestamp)
                || StringUtils.hasText(nonce)) {
            throw new SignValidationException(ResultCode.SIGNATURE_HEADER_MISSING);
        }

        long ts = parseTimestamp(timestamp);
        long now = System.currentTimeMillis() / 1000;
        if (Math.abs(now - ts) > timestampToleranceSeconds) {
            throw new SignValidationException(ResultCode.SIGNATURE_TIMESTAMP_EXPIRED);
        }

        ContentCachingRequestWrapper wrappedRequest;
        if (req instanceof ContentCachingRequestWrapper cachingRequestWrapper) {
            wrappedRequest = cachingRequestWrapper;
        } else {
            wrappedRequest = new ContentCachingRequestWrapper(req, maxBodyBytes);
        }

        byte[] body = readBody(wrappedRequest);
        if (body.length > maxBodyBytes) {
            throw new SignValidationException(ResultCode.SIGNATURE_BODY_TOO_LARGE);
        }

        String canonical = canonicalBuilder.build(req, ak, timestamp, nonce, body);
        String canonicalHash = canonicalBuilder.hash(canonical);
        String stringToSign = "HMAC-SHA256\n" + timestamp + "\n" + canonicalHash;

        String sk = secretProvider.getSk(ak);
        String expected = Hashing.hmacSha256(sk.getBytes(StandardCharsets.UTF_8))
                .hashString(stringToSign, StandardCharsets.UTF_8)
                .toString();

        if (!Objects.equals(expected, signature)) {
            throw new SignValidationException(ResultCode.SIGNATURE_INVALID);
        }

        boolean fresh = nonceGuard.checkAndStore(ak, nonce, Duration.ofSeconds(nonceTtlSeconds));
        if (!fresh) {
            throw new SignValidationException(ResultCode.SIGNATURE_NONCE_REPLAY);
        }
    }

    private byte[] readBody(ContentCachingRequestWrapper wrapped) throws IOException {
        long declared = wrapped.getContentLengthLong();
        if (declared > maxBodyBytes) {
            throw new SignValidationException(ResultCode.SIGNATURE_BODY_TOO_LARGE);
        }
        if (declared == 0L) {
            return new byte[0];
        }
        String method = wrapped.getMethod();
        if (!("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method) || "DELETE".equals(method))) {
            log.debug("[{}] 的body默认为空", method);
            return new byte[0];
        }
        return StreamUtils.copyToByteArray(wrapped.getInputStream());
    }

    private static long parseTimestamp(String raw) {
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException ex) {
            throw new SignValidationException(ResultCode.SIGNATURE_TIMESTAMP_EXPIRED);
        }
    }
}
