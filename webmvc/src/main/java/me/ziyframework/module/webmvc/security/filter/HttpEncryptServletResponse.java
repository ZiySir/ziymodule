package me.ziyframework.module.webmvc.security.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.crypto.Cipher;
import me.ziyframework.module.webmvc.security.KeyInfo;
import org.jspecify.annotations.Nullable;

/**
 * 支持加密的响应体的ServletRequest.<br />
 * created on 2025-04
 * @author ziy
 */
public final class HttpEncryptServletResponse extends HttpServletResponseWrapper {

    private final KeyInfo keyInfo;

    private @Nullable ServletOutputStream servletOutputStream;

    private @Nullable PrintWriter writer;

    public HttpEncryptServletResponse(HttpServletResponse response, KeyInfo keyInfo) {
        super(response);
        this.keyInfo = keyInfo;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (servletOutputStream == null) {
            servletOutputStream =
                    new CipherServletOutputStream(super.getOutputStream(), keyInfo.getCipher(Cipher.ENCRYPT_MODE));
        }
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
        }
        return writer;
    }
}
