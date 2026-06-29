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
import me.ziyframework.module.security.entity.PrincipalType;
import me.ziyframework.module.security.entity.RoleDoRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Redis缓存实现.
 * 优先从Redis读取权限，缓存未命中时调用 {@link #forceResolve} 加载并回写缓存.
 * created in 2026-06
 * @author ziy
 */
@RequiredArgsConstructor
@Slf4j
public class RedisAuthorityResolver implements AuthorityResolver {

    private static final RedisKey AUTHORITY_KEY = new RedisKey("authority:{}:{}");

    private final StringRedisTemplate stringRedisTemplate;

    private final JsonMapper jsonMapper;

    private final RoleDoRepository roleDoRepository;

    private final PermissionDoRepository permissionDoRepository;

    /**
     * 支持Redis缓存Authority.
     * 缓存命中时直接返回；缓存未命中时执行 {@link #forceResolve} 构建并回写缓存.
     */
    @Override
    public Collection<GrantedAuthority> resolve(PrincipalType type, long uid) {
        String key = buildKey(type, uid);
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null && !cached.isEmpty()) {
            try {
                List<String> authorityStrings = jsonMapper.readValue(cached, new TypeReference<>() {});
                return authorityStrings.stream()
                        .map(SimpleGrantedAuthority::new)
                        .map(authority -> (GrantedAuthority) authority)
                        .toList();
            } catch (Exception ex) {
                log.warn("权限缓存反序列化失败, key<{}>", key, ex);
            }
        }

        Collection<GrantedAuthority> authorities = forceResolve(type, uid);
        cache(key, authorities);
        return authorities;
    }

    /**
     * 实际加载权限的实现.
     * <p>解析顺序: 直接角色 → 沿 {@code RoleDo.parentId} 上行收集祖先角色(保留链,跳过自身禁用) →
     * 联表查询双方均启用的权限 code → 转 GrantedAuthority.</p>
     */
    public Collection<GrantedAuthority> forceResolve(PrincipalType type, long uid) {
        Set<Long> allRoleIds = roleDoRepository.getAllEnabledRoleIdByPrincipalIdAndType(type.getCode(), uid);
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
            String value = jsonMapper.writer().writeValueAsString(authorityStrings);
            stringRedisTemplate.opsForValue().set(key, value);
        } catch (Exception ex) {
            log.warn("权限缓存写入失败, key<{}>", key, ex);
        }
    }

    /**
     * 构建Redis缓存key.
     */
    private String buildKey(PrincipalType type, long uid) {
        return AUTHORITY_KEY.fmt(type.getCode(), uid);
    }
}
