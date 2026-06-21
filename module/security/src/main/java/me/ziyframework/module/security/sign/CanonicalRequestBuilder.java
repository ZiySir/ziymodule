package me.ziyframework.module.security.sign;

import com.google.common.hash.Hashing;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 构造 Sigv4 风格的规范化请求串.
 */
public final class CanonicalRequestBuilder {

    private static final String EMPTY_BODY_SHA256 =
            Hashing.sha256().hashBytes(new byte[0]).toString();

    public CanonicalRequestBuilder() {}

    public String build(HttpServletRequest req, String ak, String timestamp, String nonce, byte[] body) {
        String method = req.getMethod().toUpperCase(Locale.ROOT);
        String path = req.getRequestURI();
        String sortedQuery = sortedQuery(req.getParameterMap());
        String bodyHash = body.length == 0
                ? EMPTY_BODY_SHA256
                : Hashing.sha256().hashBytes(body).toString();

        return String.join("\n", method, path, sortedQuery, timestamp, nonce, ak, bodyHash);
    }

    public String hash(String canonicalRequest) {
        return Hashing.sha256()
                .hashString(canonicalRequest, StandardCharsets.UTF_8)
                .toString();
    }

    private static String sortedQuery(Map<String, String[]> parameterMap) {
        if (parameterMap.isEmpty()) {
            return "";
        }
        return parameterMap.entrySet().stream()
                .sorted(Entry.comparingByKey())
                .filter(entry -> {
                    String[] value = entry.getValue();
                    return value.length >= 1;
                })
                .map(entry -> entry.getKey() + "=" + entry.getValue()[0])
                .collect(Collectors.joining("&"));
    }
}
