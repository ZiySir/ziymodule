package me.ziyframework.module.security.config;

import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
import com.blazebit.persistence.spring.data.repository.config.EnableBlazeRepositories;
import lombok.RequiredArgsConstructor;
import me.ziyframework.boot.redis.RedisHolder;
import me.ziyframework.module.security.auth.AuthManager;
import me.ziyframework.module.security.auth.AuthorityResolver;
import me.ziyframework.module.security.auth.RedisAuthorityResolver;
import me.ziyframework.module.security.entity.PermissionDoRepository;
import me.ziyframework.module.security.entity.RoleDoRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import tools.jackson.databind.json.JsonMapper;

/**
 * 安全模块的自动配置类.
 * created in 2026-05
 * @author ziy
 */
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityProperties.class)
@EntityScan(basePackages = "me.ziyframework.module.security.entity")
@EnableEntityViews(basePackages = "me.ziyframework.module.security.entity")
@EnableBlazeRepositories(basePackages = "me.ziyframework.module.security.entity")
public class SecurityAutoConfiguration {

    private final SecurityProperties securityProperties;

    /**
     * 默认注入一个Redis的权限加载器.
     */
    @Bean
    public AuthorityResolver authorityResolver(
            JsonMapper jsonMapper, RoleDoRepository roleDoRepository, PermissionDoRepository permissionDoRepository) {
        StringRedisTemplate stringRedisTemplate =
                RedisHolder.getStringRedisTemplate(securityProperties.getAloneRedis());
        return new RedisAuthorityResolver(stringRedisTemplate, jsonMapper, roleDoRepository, permissionDoRepository);
    }

    /**
     * SpringSecurity的 安全上下文 的Repository仓库.
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /**
     * 认证管理器.
     */
    @Bean
    public AuthManager authManager(SecurityContextRepository securityContextRepository) {
        return new AuthManager(securityContextRepository);
    }

    /**
     * security过滤链.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
        return http.authorizeHttpRequests(auth -> {
                    auth.requestMatchers(securityProperties.getDeny().toArray(String[]::new))
                            .denyAll()
                            .requestMatchers(securityProperties.getPermit().toArray(String[]::new))
                            .permitAll()
                            .anyRequest()
                            .authenticated();
                })
                .securityContext(config -> {
                    config.securityContextRepository(securityContextRepository);
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .jee(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }
}
