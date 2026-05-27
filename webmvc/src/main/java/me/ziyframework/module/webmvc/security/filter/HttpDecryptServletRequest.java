package me.ziyframework.module.webmvc.security.filter;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.crypto.Cipher;
import me.ziyframework.module.webmvc.security.KeyInfo;
import org.jspecify.annotations.Nullable;

/**
 * 支持请求体解密的ServletRequest.<br />
 * created on 2025-04
 *
 * @author ziy
 */
public final class HttpDecryptServletRequest extends HttpServletRequestWrapper {

    private final KeyInfo keyInfo;

    private @Nullable CipherServletInputStream servletInputStream;

    HttpDecryptServletRequest(HttpServletRequest request, KeyInfo keyInfo) {
        super(request);
        this.keyInfo = keyInfo;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (servletInputStream == null) {
            servletInputStream =
                    new CipherServletInputStream(super.getInputStream(), keyInfo.getCipher(Cipher.DECRYPT_MODE));
        }
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ServletInputStream inputStream = this.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream, getCharacterEncoding()));
    }
}
