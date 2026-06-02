package me.ziyframework.module.security.auth;

/**
 * 登录的数据模型.
 * created in 2026-05
 * @author ziy
 */
public record LoginModel(

        /**
         * 登录类型，该字段可用于区分独立账号体系.
         */
        String type,

        /**
         * 登录唯一标识,整个体系中将以此定位一个会话.
         */
        Object loginId,

        /**
         * 扩展数据存储.
         */
        Object extra) {}
