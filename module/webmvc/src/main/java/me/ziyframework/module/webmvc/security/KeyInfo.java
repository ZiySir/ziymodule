package me.ziyframework.module.webmvc.security;

import com.google.common.io.BaseEncoding;
import javax.crypto.Cipher;
import me.ziyframework.module.webmvc.security.cipher.HttpSymmetricCipher;
import me.ziyframework.module.webmvc.security.exchange.SecretExchange;

/**
 * 用于对称解密的信息类.
 * created in 2026-05
 * @author ziy
 */
public class KeyInfo {

    private final HttpSymmetricCipher httpSymmetricCipher;

    private final SecretExchange secretExchange;

    private final String clientPublicKey;

    private final byte[] iv;

    private final String serverKeyId;

    public KeyInfo(
            HttpSymmetricCipher httpSymmetricCipher,
            SecretExchange secretExchange,
            String clientPublicKey,
            String iv,
            String serverKeyId) {
        this.httpSymmetricCipher = httpSymmetricCipher;
        this.secretExchange = secretExchange;
        this.clientPublicKey = clientPublicKey;
        this.iv = BaseEncoding.base64Url().decode(iv);
        this.serverKeyId = serverKeyId;
    }

    /**
     * 获取Cipher对象.
     * @param mode 模式
     */
    public Cipher getCipher(int mode) {
        byte[] sharedSecret = secretExchange.getSharedSecret(clientPublicKey, serverKeyId);
        try {
            byte[] secret = httpSymmetricCipher.deriveKey(sharedSecret);
            return httpSymmetricCipher.createCipher(mode, secret, iv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
