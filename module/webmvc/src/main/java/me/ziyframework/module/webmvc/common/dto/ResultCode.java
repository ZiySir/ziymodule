package me.ziyframework.module.webmvc.common.dto;

import me.ziyframework.boot.core.i18n.MessageSourceHolder;

/**
 * 结果码枚举.
 *
 * @author ziy
 */
public record ResultCode(int code, String msg) {

    public static final ResultCode OK = new ResultCode(200_0, "ok");

    /**
     * 通用错误.
     */
    public static final ResultCode FAIL = new ResultCode(500_0, "fail");

    /**
     * 请求参数错误.
     */
    public static final ResultCode BAD_REQUEST = new ResultCode(400_00, "bad request");

    /**
     * 请勿重复提交.
     */
    public static final ResultCode IDEMPOTENT = new ResultCode(400_01, "duplicate request");

    /**
     * 请求方法不允许.
     */
    public static final ResultCode METHOD_NOT_ALLOWED = new ResultCode(405_00, "request method is not allowed");

    /**
     * 请求资源不存在.
     */
    public static final ResultCode NOT_FOUND = new ResultCode(404_00, "not found");

    /**
     * 未授权访问.
     */
    public static final ResultCode UNAUTHORIZED = new ResultCode(401_00, "unauthorized");

    /**
     * 禁止访问.
     */
    public static final ResultCode FORBIDDEN = new ResultCode(403_00, "forbidden");

    /**
     * 缺少签名头.
     */
    public static final ResultCode SIGNATURE_HEADER_MISSING = new ResultCode(401_01, "missing signature header");

    /**
     * 签名时间戳过期.
     */
    public static final ResultCode SIGNATURE_TIMESTAMP_EXPIRED = new ResultCode(401_02, "signature timestamp expired");

    /**
     * 非法的 accessKey.
     */
    public static final ResultCode SIGNATURE_AK_INVALID = new ResultCode(401_03, "invalid access key");

    /**
     * 签名不匹配.
     */
    public static final ResultCode SIGNATURE_INVALID = new ResultCode(401_04, "signature mismatch");

    /**
     * 请求重放.
     */
    public static final ResultCode SIGNATURE_NONCE_REPLAY = new ResultCode(401_05, "request replay");

    /**
     * 请求体过大.
     */
    public static final ResultCode SIGNATURE_BODY_TOO_LARGE = new ResultCode(413_00, "request body too large");

    public ResultCode(int code, String msg) {
        this.code = code;
        this.msg = MessageSourceHolder.i18n(msg);
    }
}
