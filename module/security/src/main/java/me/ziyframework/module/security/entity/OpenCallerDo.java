package me.ziyframework.module.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.ziyframework.boot.data.spring.jpa.entity.JpaRelationalBaseEntity;
import org.jspecify.annotations.Nullable;

/**
 * 开放平台调用方.
 */
@Entity
@Table(name = "open_caller")
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenCallerDo extends JpaRelationalBaseEntity {

    @Column(length = 64, nullable = false, unique = true, comment = "accessKey")
    private @Nullable String ak;

    @Column(length = 128, nullable = false, comment = "secretKey，需要妥善保存")
    private @Nullable String sk;

    @Column(length = 128, nullable = false, comment = "开放平台的名称")
    private @Nullable String name;

    @Column(nullable = false, options = "default false")
    private @Nullable Boolean enabled;
}
