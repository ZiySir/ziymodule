package me.ziyframework.module.security.entity;

import me.ziyframework.boot.data.spring.jpa.entity.JpaRelationalBaseEntity;

/**
 * 后台用户.
 * <p><b>内部所有关联统一使用id,对外暴露统一使用uid</b></p>
 */
public class BackendUser extends JpaRelationalBaseEntity {

    /**
     * 用户对外暴露的唯一标识.
     */
    private String uid;

    /**
     * 是否禁用.
     */
    private boolean disabled;
}
