package me.ziyframework.module.security.sign.nonce;

import java.time.Duration;
import me.ziyframework.module.security.sign.SignRedisKeys;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 基于 Redis SETNX 的 Nonce 守卫.
 */
public class RedisCallerNonceGuard implements NonceGuard {

    private final StringRedisTemplate redisTemplate;

    public RedisCallerNonceGuard(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 检查并存储 nonce. 首次返回 true.
     */
    @Override
    public boolean checkAndStore(String ak, String nonce, Duration ttl) {
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(SignRedisKeys.NONCE.fmt(ak, nonce), "1", ttl);
        return Boolean.TRUE.equals(ok);
    }
}
