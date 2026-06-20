package me.ziyframework.module.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.ziyframework.boot.data.spring.jpa.entity.LogicJpaRelationalBaseEntity;
import org.jspecify.annotations.Nullable;

/**
 * 后台用户.
 * <p><b>内部所有关联统一使用id,对外暴露统一使用uid</b></p>
 */
@Entity
@Table(name = "backend_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class BackendUserDo extends LogicJpaRelationalBaseEntity {

    /**
     * 用户对外暴露的唯一标识.
     */
    private String uid;

    /**
     * 用户名.
     */
    private String username;

    /**
     * 密码(Argon2id).
     */
    private String password;

    /**
     * 别名(昵称).
     */
    private String nickName;

    /**
     * 是否禁用.
     */
    private @Nullable Boolean disabled;
}
