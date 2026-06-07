package me.ziyframework.module.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * .
 * created in 2026-06
 * @author ziy
 */
@ConfigurationProperties(prefix = "module.security")
@Getter
@Setter
public class SessionProperties {

    private String aloneRedis = "default";
}
