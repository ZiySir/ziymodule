package me.ziyframework.module.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.ziyframework.boot.data.spring.jpa.entity.JpaRelationalBaseEntity;
import org.jspecify.annotations.Nullable;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public class JpaLogicRelationalBaseEntity extends JpaRelationalBaseEntity {

    @Column(columnDefinition = "bool default false", nullable = false, comment = "是否逻辑删除")
    private @Nullable Boolean deleted;
}
