package me.ziyframework.example.api;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** 客户端签名计算（与 {@link me.ziyframework.module.security.sign.CanonicalRequestBuilder} 字段严格镜像）. */
public final class SignClient {

    private static final String EMPTY_BODY_SHA256 =
            Hashing.sha256().hashBytes(new byte[0]).toString();

    private SignClient() {}

    /**
     * 计算签名并返回 4 个签名头部.
     * @param ak    Access Key
     * @param sk    Secret Key
     * @param method  HTTP 方法
     * @param path    请求路径（不含 query）
     * @param query   Query 参数（key → values, 取首个）
     * @param body    请求体字节（空 body 传 new byte[0]）
     */
    public static Map<String, String> signHeaders(
            String ak, String sk, String method, String path, Map<String, String[]> query, byte[] body) {
        long timestamp = System.currentTimeMillis() / 1000;
        String nonce = UUID.randomUUID().toString();
        String bodyHash = body.length == 0
                ? EMPTY_BODY_SHA256
                : Hashing.sha256().hashBytes(body).toString();

        String sortedQuery = query == null
                ? ""
                : query.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .filter(e -> e.getValue().length >= 1)
                        .map(e -> e.getKey() + "=" + e.getValue()[0])
                        .collect(Collectors.joining("&"));
        String canonical = String.join(
                "\n",
                method.toUpperCase(Locale.ROOT),
                path,
                sortedQuery,
                String.valueOf(timestamp),
                nonce,
                ak,
                bodyHash);
        String canonicalHash =
                Hashing.sha256().hashString(canonical, StandardCharsets.UTF_8).toString();
        String stringToSign = "HMAC-SHA256\n" + timestamp + "\n" + canonicalHash;
        String signature = Hashing.hmacSha256(sk.getBytes(StandardCharsets.UTF_8))
                .hashString(stringToSign, StandardCharsets.UTF_8)
                .toString();

        return Map.of(
                "X-Sign-AK", ak,
                "X-Sign-Timestamp", String.valueOf(timestamp),
                "X-Sign-Nonce", nonce,
                "X-Sign-Signature", signature);
    }
}
