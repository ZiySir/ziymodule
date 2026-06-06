package me.ziyframework.module.security.config;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "module.security")
public class SecurityProperties {

    /**
     * 允许直接访问的url.
     */
    private Set<String> permit = new HashSet<>();

    /**
     * 拒绝访问的url.
     */
    private Set<String> deny = new HashSet<>();
}
