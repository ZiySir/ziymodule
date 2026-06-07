package me.ziyframework.module.security.auth;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ziyframework.boot.redis.RedisKey;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Redis缓存实现.
 * 优先从Redis读取权限，缓存未命中时调用 {@link #forceResolve(LoginModel)} 加载并回写缓存.
 * created in 2026-06
 * @author ziy
 */
@RequiredArgsConstructor
@Slf4j
public abstract class RedisAuthorityResolver implements AuthorityResolver {

    private static final RedisKey AUTHORITY_KEY = new RedisKey("authority:{}:{}");

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    /**
     * 支持Redis缓存Authority.
     * 缓存命中时直接返回；缓存未命中时执行 {@link #forceResolve(LoginModel)} 构建并回写缓存.
     */
    @Override
    public Collection<GrantedAuthority> resolve(LoginModel loginModel) {
        String key = buildKey(loginModel);
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null && !cached.isEmpty()) {
            try {
                List<String> authorityStrings = objectMapper.readValue(cached, new TypeReference<>() {});
                return authorityStrings.stream()
                        .map(SimpleGrantedAuthority::new)
                        .map(authority -> (GrantedAuthority) authority)
                        .toList();
            } catch (Exception ex) {
                log.warn("权限缓存反序列化失败, key<{}>", key, ex);
            }
        }

        Collection<GrantedAuthority> authorities = forceResolve(loginModel);
        cache(key, authorities);
        return authorities;
    }

    /**
     * 实际加载权限的实现，由子类提供具体的加载逻辑.
     */
    public abstract Collection<GrantedAuthority> forceResolve(LoginModel loginModel);

    /**
     * 将权限写入Redis缓存.
     */
    private void cache(String key, Collection<? extends GrantedAuthority> authorities) {
        List<String> authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .toList();
        try {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writer().writeValueAsString(authorityStrings));
        } catch (Exception ex) {
            log.warn("权限缓存写入失败, key<{}>", key, ex);
        }
    }

    /**
     * 构建Redis缓存key.
     */
    private String buildKey(LoginModel loginModel) {
        return AUTHORITY_KEY.fmt(loginModel.type(), loginModel.loginId());
    }
}
