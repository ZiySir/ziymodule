package me.ziyframework.module.security.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ziyframework.boot.redis.RedisKey;
import me.ziyframework.module.security.entity.PermissionDoRepository;
import me.ziyframework.module.security.entity.RoleDoRepository;
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
public class RedisAuthorityResolver implements AuthorityResolver {

    private static final RedisKey AUTHORITY_KEY = new RedisKey("authority:{}:{}");

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    private final RoleDoRepository roleDoRepository;

    private final PermissionDoRepository permissionDoRepository;

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
     * 实际加载权限的实现.
     * <p>解析顺序: 直接角色 → 沿 {@code RoleDo.parentId} 上行收集祖先角色(保留链,跳过自身禁用) →
     * 联表查询双方均启用的权限 code → 转 GrantedAuthority.</p>
     */
    public Collection<GrantedAuthority> forceResolve(LoginModel loginModel) {
        Set<Long> allRoleIds = roleDoRepository.getAllEnabledRoleIdByPrincipalIdAndType(
                loginModel.type().getCode(), loginModel.userId());
        if (allRoleIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> permissionCodes = permissionDoRepository.getEnabledCodeByRoleIdIn(allRoleIds);
        return permissionCodes.stream()
                .map(SimpleGrantedAuthority::new)
                .map(authority -> (GrantedAuthority) authority)
                .toList();
    }

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
        return AUTHORITY_KEY.fmt(loginModel.type().getCode(), loginModel.userId());
    }
}
