package me.ziyframework.module.security.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

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

    /**
     * 整个Security模块使用的redis数据源.
     */
    private String aloneRedis = "default";

    /**
     * 会话配置.
     */
    private SessionConfig session = new SessionConfig();

    /**
     * AK/SK 签名子节点.
     */
    private Sign sign = new Sign();

    /**
     * 签名机制配置.
     */
    @Getter
    @Setter
    public static class Sign {

        /**
         * 是否启用.
         */
        private boolean enabled = false;

        /**
         * 时间偏差容忍间隔(秒).
         */
        private long timestampToleranceSeconds = 300;

        /**
         * Nonce TTL 秒数.
         */
        private long nonceTtlSeconds = 600;

        /**
         * SK 缓存 TTL 秒数.
         */
        private long skCacheTtlSeconds = 1800;

        /**
         * Request Body 最大允许的大小.
         */
        private DataSize maxBody = DataSize.ofMegabytes(1);

        /**
         * 需要应用的路径.
         */
        private List<String> pattern = List.of("/api/*");
    }

    /**
     * 会话相关配置.
     */
    @Getter
    @Setter
    public static class SessionConfig {

        /**
         * 是否启动会话.
         */
        private boolean enabled = false;
    }
}
