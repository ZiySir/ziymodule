package me.ziyframework.module.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.ziyframework.boot.data.spring.jpa.entity.JpaRelationalBaseEntity;

/**
 * 开放平台调用方.
 */
@Entity
@Table(name = "open_caller")
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenCallerDo extends JpaRelationalBaseEntity {

    @Column(length = 64, nullable = false, unique = true)
    private String ak;

    @Column(length = 128, nullable = false)
    private String sk;

    @Column(length = 128, nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean enabled;
}
