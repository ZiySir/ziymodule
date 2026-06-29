package me.ziyframework.module.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.ziyframework.boot.data.spring.jpa.entity.JpaRelationalBaseEntity;
import org.hibernate.annotations.SoftDelete;
import org.jspecify.annotations.Nullable;

/**
 * 后台用户.
 * <p><b>内部所有关联统一使用id,对外暴露统一使用uid</b></p>
 */
@Entity
@Table(name = "backend_user")
@Data
@EqualsAndHashCode(callSuper = true)
@SoftDelete
public class BackendUserDo extends JpaRelationalBaseEntity {

    @Column(length = 128, nullable = false, unique = true, comment = "用户对外暴露的唯一标识")
    private @Nullable String uid;

    @Column(length = 32, nullable = false, unique = true, comment = "用户账号")
    private @Nullable String username;

    @Column(nullable = false, options = "default false", comment = "用户锁定状态(默认不锁定)")
    private @Nullable Boolean locked;

    @Column(length = 512, nullable = false, comment = "密码(Argon2id)")
    private @Nullable String password;

    @Column(length = 32, comment = "别名(昵称)")
    private @Nullable String nickName;

    @Column(options = "default false", nullable = false, comment = "当前账号是否禁用")
    private @Nullable Boolean disabled;
}
