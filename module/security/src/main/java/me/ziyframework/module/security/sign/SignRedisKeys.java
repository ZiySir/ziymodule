package me.ziyframework.module.security.sign;

import me.ziyframework.boot.redis.RedisKey;

/** 签名机制 Redis Key 常量. */
public final class SignRedisKeys {

    private SignRedisKeys() {}

    public static final RedisKey SK = new RedisKey("sign:ak:{}");

    public static final RedisKey NONCE = new RedisKey("sign:nonce:{}:{}");
}
