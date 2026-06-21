package me.ziyframework.module.security.sign.exception;

/** AK 不存在或已禁用. */
public class CallerNotFoundException extends RuntimeException {

    public CallerNotFoundException(String ak) {
        super("caller not found: " + ak);
    }
}
