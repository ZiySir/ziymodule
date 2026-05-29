package me.ziyframework.module.data.config;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import me.ziyframework.module.data.blaze.Blaze;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class DataAutoConfiguration {

    /**
     * 创建CriteriaBuilderFactory.
     */
    @Bean
    public CriteriaBuilderFactory createCriteriaBuilderFactory(EntityManagerFactory entityManagerFactory) {
        CriteriaBuilderConfiguration config = Criteria.getDefault();
        return config.createCriteriaBuilderFactory(entityManagerFactory);
    }

    /**
     * 创建EntityViewManager.
     */
    @Bean
    public EntityViewManager createEntityViewManager(
            CriteriaBuilderFactory cbf, EntityViewConfiguration entityViewConfiguration) {
        return entityViewConfiguration.createEntityViewManager(cbf);
    }

    /**
     * BlazePersistence辅助类.
     */
    @Bean
    public Blaze blaze(CriteriaBuilderFactory cbf, EntityViewManager evm, EntityManager em) {
        return new Blaze(cbf, evm, em);
    }
}
