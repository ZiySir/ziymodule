package me.ziyframework.module.security.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.ziyframework.framework.enumeration.BaseEnum;

/**
 * 主体类型,用于区分多账号体系.
 * 入库走 {@link #getCode()},由 BaseEnumJavaType 自动处理.
 * created in 2026-06
 * @author ziy
 */
@Getter
@RequiredArgsConstructor
public enum PrincipalType implements BaseEnum<PrincipalType> {

    /**
     * 后台用户.
     */
    BACKEND(1, "后台用户");

    private final int code;

    private final String desc;
}
