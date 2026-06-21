package me.ziyframework.module.security.sign.nonce;

import java.time.Duration;

/**
 * 防重放 Nonce 守卫.
 */
public interface NonceGuard {

    /**
     * 首次返回 true,重放返回 false.
     */
    boolean checkAndStore(String ak, String nonce, Duration ttl);
}
