package me.ziyframework.service.user.entity;

import java.util.Optional;
import me.ziyframework.boot.data.spring.jpa.repository.SpringDataJpaBaseRepository;
import org.springframework.stereotype.Repository;

/** {@link OpenCallerDo}的Repository. */
@Repository
public interface OpenCallerDoRepository extends SpringDataJpaBaseRepository<OpenCallerDo> {

    Optional<OpenCallerDo> findByAk(String ak);
}
