package me.ziyframework.module.security.sign.provider;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ziyframework.module.security.entity.OpenCallerDo;
import me.ziyframework.module.security.entity.OpenCallerDoRepository;
import me.ziyframework.module.security.sign.SignRedisKeys;
import me.ziyframework.module.security.sign.exception.CallerNotFoundException;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis 缓存 + DB 回源查询 SK.
 * @author ziy
 */
@RequiredArgsConstructor
@Slf4j
public class RedisCallerSecretProvider implements CallerSecretProvider {

    private final StringRedisTemplate redisTemplate;

    private final OpenCallerDoRepository repository;

    private final long cacheTtlSeconds;

    /**
     * 根据 AK 查询 SK. ak 不存在或已禁用时抛 CallerNotFoundException.
     */
    @Override
    public String getSk(String ak) {
        String cached = safeRedisGet(ak);
        if (cached != null) {
            return cached;
        }
        OpenCallerDo caller = repository
                .findByAk(ak)
                .filter(OpenCallerDo::getEnabled)
                .orElseThrow(() -> new CallerNotFoundException(ak));
        safeRedisPut(ak, caller.getSk());
        return caller.getSk();
    }

    private @Nullable String safeRedisGet(String ak) {
        try {
            return redisTemplate.opsForValue().get(SignRedisKeys.SK.fmt(ak));
        } catch (Exception ex) {
            log.atWarn().setCause(ex).addArgument(ak).log("secretKey 从Redis获取失败 {}");
            return null;
        }
    }

    private void safeRedisPut(String ak, String sk) {
        try {
            redisTemplate.opsForValue().set(SignRedisKeys.SK.fmt(ak), sk, cacheTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.atWarn().setCause(ex).addArgument(ak).log("secretKey 向Redis写入失败 {}");
            // 缓存写失败不影响主流程
        }
    }
}
