package me.ziyframework.module.security.auth;

import me.ziyframework.module.security.entity.PrincipalType;
import org.jspecify.annotations.Nullable;

/**
 * 登录的数据模型.
 * created in 2026-05
 * @author ziy
 */
public record LoginModel(

        /*
         主体类型，多账号体系下区分使用.
        */
        PrincipalType type,

        /*
         登录唯一标识,整个体系中将以此定位一个会话.
        */
        Long userId,

        /*
         扩展数据存储.
        */
        @Nullable Object extra) {}
