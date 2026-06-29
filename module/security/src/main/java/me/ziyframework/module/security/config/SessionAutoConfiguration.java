package me.ziyframework.module.security.config;

import lombok.RequiredArgsConstructor;
import me.ziyframework.boot.core.SpringHolder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;
import tools.jackson.databind.ObjectMapper;

/**
 * Spring Session相关自动配置.
 * created in 2026-06
 * @author ziy
 */
@AutoConfiguration
@RequiredArgsConstructor
@EnableRedisIndexedHttpSession
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty("module.security.session.enabled")
public class SessionAutoConfiguration {

    private final SecurityProperties securityProperties;

    /**
     * Spring Session的Redis数据源.
     */
    @Bean
    @SpringSessionRedisConnectionFactory
    public RedisConnectionFactory sessionRedisConnectionFactory() {
        String datasourceName = securityProperties.getAloneRedis();
        return SpringHolder.getBean(datasourceName, RedisConnectionFactory.class);
    }

    /**
     * 重写session 会话的Redis的序列化.
     */
    @Bean
    public SessionRepositoryCustomizer<RedisIndexedSessionRepository> customRedisSerializer(ObjectMapper objectMapper) {
        return sessionRepository -> {
            sessionRepository.setDefaultSerializer(new GenericJacksonJsonRedisSerializer(objectMapper));
        };
    }
}
