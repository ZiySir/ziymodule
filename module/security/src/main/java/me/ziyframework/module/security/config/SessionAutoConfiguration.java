package me.ziyframework.module.security.config;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import me.ziyframework.boot.redis.RedisHolder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;

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
        RedisTemplate<Object, Object> redisTemplate = RedisHolder.getRedisTemplate(datasourceName);
        return Preconditions.checkNotNull(redisTemplate.getConnectionFactory(), "%s redis数据源不存在", datasourceName);
    }
}
