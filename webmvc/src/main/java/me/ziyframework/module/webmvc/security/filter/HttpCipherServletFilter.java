package me.ziyframework.module.webmvc.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import me.ziyframework.framework.tuple.Tuple2;
import me.ziyframework.framework.tuple.Tuples;
import me.ziyframework.module.webmvc.common.WebHolder;
import me.ziyframework.module.webmvc.common.dto.Result;
import me.ziyframework.module.webmvc.common.dto.ResultCode;
import me.ziyframework.module.webmvc.common.exception.ClientException;
import me.ziyframework.module.webmvc.security.HttpEncrypt;
import me.ziyframework.module.webmvc.security.KeyInfo;
import me.ziyframework.module.webmvc.security.SecurityHttpHeaders;
import me.ziyframework.module.webmvc.security.cipher.HttpSymmetricCipher;
import me.ziyframework.module.webmvc.security.exchange.SecretExchange;
import org.jspecify.annotations.Nullable;
import org.springframework.web.method.HandlerMethod;

/**
 * 接口响应体加密拦截器.<br />
 * created on 2025-04
 *
 * @author ziy
 */
@Slf4j
public class HttpCipherServletFilter implements Filter {

    private final SecretExchange secretExchange;

    private final HttpSymmetricCipher httpSymmetricCipher;

    public HttpCipherServletFilter(SecretExchange secretExchange, HttpSymmetricCipher httpSymmetricCipher) {
        this.secretExchange = secretExchange;
        this.httpSymmetricCipher = httpSymmetricCipher;
    }

    /**
     * 拦截响应体并加密响应体.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        Tuple2<HttpServletRequest, HttpServletResponse> tuple;
        try {
            tuple = delegateRequestAndResponse((HttpServletRequest) request, (HttpServletResponse) response);
        } catch (Exception ex) {
            log.error("Http接口加解密出现异常", ex);
            WebHolder.resetResponse(Result.of(ResultCode.BAD_REQUEST));
            return;
        }
        chain.doFilter(tuple.v1(), tuple.v2());
    }

    /**
     * 推断当前接口是否声明了加密或解密，并返回对应的Request和Response.
     */
    private Tuple2<HttpServletRequest, HttpServletResponse> delegateRequestAndResponse(
            HttpServletRequest request, HttpServletResponse response) {
        HttpServletRequest req = request;
        HttpServletResponse resp = response;
        // 获取处理实际请求的方法包装对象HandlerMethod,并根据注解进行推断
        HttpEncrypt httpEncrypt = getHttpEncrypt(request);
        if (httpEncrypt == null) {
            return Tuples.of(request, response);
        }
        KeyInfo keyInfo = getKeyInfo();

        if (httpEncrypt.decrypt()) {
            // 解密请求体
            req = new HttpDecryptServletRequest(request, keyInfo);
        }
        if (httpEncrypt.encrypt()) {
            // 加密响应
            resp = new HttpEncryptServletResponse(response, keyInfo);
        }
        return Tuples.of(req, resp);
    }

    /**
     * 解析请求头中的密钥信息.
     *
     * @return _1: 共享密钥, _2: 加密向量
     */
    private KeyInfo getKeyInfo() {
        final String clientPublicKey = WebHolder.getRequestHeader(SecurityHttpHeaders.X_KEY);
        final String iv = WebHolder.getRequestHeader(SecurityHttpHeaders.X_NONCE);
        final String serverKeyId = WebHolder.getRequestHeader(SecurityHttpHeaders.X_KEY_ID);

        if (clientPublicKey == null || iv == null || serverKeyId == null) {
            throw new ClientException("请求头中缺少客户端公钥/iv/serverKeyId");
        }
        return new KeyInfo(httpSymmetricCipher, secretExchange, clientPublicKey, iv, serverKeyId);
    }

    /**
     * 获取当前接口的加解密注解,尝试从方法和类上获取.
     */
    private @Nullable HttpEncrypt getHttpEncrypt(HttpServletRequest request) {
        HandlerMethod handlerMethod = WebHolder.getHandlerMethod(request);
        HttpEncrypt httpEncrypt = handlerMethod.getMethodAnnotation(HttpEncrypt.class);
        if (httpEncrypt == null) {
            httpEncrypt = handlerMethod.getBeanType().getAnnotation(HttpEncrypt.class);
        }
        return httpEncrypt;
    }
}
