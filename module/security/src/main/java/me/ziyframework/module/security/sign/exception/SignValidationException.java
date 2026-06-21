package me.ziyframework.module.security.sign.exception;

import lombok.Getter;
import me.ziyframework.module.webmvc.common.dto.ResultCode;

/**
 * 签名校验失败.
 */
@Getter
public class SignValidationException extends RuntimeException {

    /**
     * -- GETTER --
     * 获取错误码.
     */
    private final ResultCode code;

    public SignValidationException(ResultCode code) {
        super(code.msg());
        this.code = code;
    }
}
