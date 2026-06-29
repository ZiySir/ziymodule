package me.ziyframework.module.security.config;

import me.ziyframework.module.security.entity.OpenCallerDoRepository;
import me.ziyframework.module.security.sign.CanonicalRequestBuilder;
import me.ziyframework.module.security.sign.SignErrorResponseWriter;
import me.ziyframework.module.security.sign.SignValidatorServletFilter;
import me.ziyframework.module.security.sign.nonce.NonceGuard;
import me.ziyframework.module.security.sign.nonce.RedisCallerNonceGuard;
import me.ziyframework.module.security.sign.provider.AbstractRedisCallerSecretProvider;
import me.ziyframework.module.security.sign.provider.CallerSecretProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.json.JsonMapper;

/** AK/SK 鉴权装配. */
@AutoConfiguration
@ConditionalOnProperty(prefix = "module.security.sign", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SignAutoConfiguration {

    /**
     * 规范化请求构造器.
     */
    @Bean
    public CanonicalRequestBuilder canonicalRequestBuilder() {
        return new CanonicalRequestBuilder();
    }

    /**
     * 签名错误响应写入器.
     */
    @Bean
    public SignErrorResponseWriter signErrorResponseWriter(JsonMapper jsonMapper) {
        return new SignErrorResponseWriter(jsonMapper);
    }

    /**
     * Nonce 防重放守卫.
     */
    @Bean
    public NonceGuard nonceGuard(StringRedisTemplate redisTemplate) {
        return new RedisCallerNonceGuard(redisTemplate);
    }

    /**
     * 调用方密钥提供器.
     */
    @Bean
    public CallerSecretProvider callerSecretProvider(
            StringRedisTemplate redisTemplate, OpenCallerDoRepository repository, SecurityProperties properties) {
        SecurityProperties.Sign sign = properties.getSign();
        return new AbstractRedisCallerSecretProvider(redisTemplate, repository, sign.getSkCacheTtlSeconds());
    }

    /**
     * 签名校验过滤器.
     */
    @Bean
    public SignValidatorServletFilter signValidatorServletFilter(
            SecurityProperties properties,
            CanonicalRequestBuilder canonicalBuilder,
            CallerSecretProvider secretProvider,
            NonceGuard nonceGuard,
            SignErrorResponseWriter errorWriter) {
        SecurityProperties.Sign sign = properties.getSign();
        return new SignValidatorServletFilter(
                sign.getTimestampToleranceSeconds(),
                sign.getNonceTtlSeconds(),
                (int) sign.getMaxBody().toBytes(),
                canonicalBuilder,
                secretProvider,
                nonceGuard,
                errorWriter);
    }

    /**
     * 过滤器注册.
     */
    @Bean
    public FilterRegistrationBean<SignValidatorServletFilter> signFilterRegistration(
            SignValidatorServletFilter filter, SecurityProperties properties) {
        FilterRegistrationBean<SignValidatorServletFilter> bean = new FilterRegistrationBean<>(filter);
        String[] urlPattern = properties.getSign().getPattern().toArray(String[]::new);
        bean.addUrlPatterns(urlPattern);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return bean;
    }
}
