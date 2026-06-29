package me.ziyframework.module.security.config;

import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
import com.blazebit.persistence.spring.data.repository.config.EnableBlazeRepositories;
import com.password4j.Argon2Function;
import com.password4j.types.Argon2;
import lombok.RequiredArgsConstructor;
import me.ziyframework.boot.redis.RedisHolder;
import me.ziyframework.module.security.auth.AbstractLoginAuthenticationProvider;
import me.ziyframework.module.security.auth.AuthManager;
import me.ziyframework.module.security.entity.PermissionDoRepository;
import me.ziyframework.module.security.entity.RoleDoRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import tools.jackson.databind.json.JsonMapper;

/**
 * 安全模块的自动配置类.
 * created in 2026-05
 *
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
     * Argon2id 密码编码器.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        Argon2Function instance = Argon2Function.getInstance(65536, 3, 2, 32, Argon2.ID);
        return new Argon2Password4jPasswordEncoder(instance);
    }

    /**
     * 认证管理器: 将认证逻辑全面委派给 LoginAuthenticationProvider.
     */
    @Bean
    public AuthenticationManager authenticationManager(AbstractLoginAuthenticationProvider abstractLoginAuthenticationProvider) {
        return new ProviderManager(abstractLoginAuthenticationProvider);
    }

    /**
     * 认证管理器门面(AuthManager 内部持有,用于 authenticate + 写 context).
     */
    @Bean
    public AuthManager authManager(
            AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository) {
        return new AuthManager(authenticationManager, securityContextRepository);
    }

    /**
     * SpringSecurity的 安全上下文 的Repository仓库.
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
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
