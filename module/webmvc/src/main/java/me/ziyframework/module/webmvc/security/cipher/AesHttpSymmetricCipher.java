package me.ziyframework.module.webmvc.security.cipher;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Aes的cipher工厂实现.
 * created in 2026-05
 * @author ziy
 */
public class AesHttpSymmetricCipher implements HttpSymmetricCipher {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    /**
     * {@inheritDoc}
     */
    @Override
    public Cipher createCipher(int mode, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        cipher.init(mode, keySpec, spec);
        return cipher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] deriveKey(byte[] sharedKey) throws Exception {
        return deriveKey(sharedKey, "AES");
    }
}
