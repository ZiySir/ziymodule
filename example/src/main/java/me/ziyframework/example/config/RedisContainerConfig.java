package me.ziyframework.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.testcontainers.containers.GenericContainer;

/** 通过 TestContainer 启动 Redis 并把 host/port 注入到系统属性. */
@Slf4j
@AutoConfiguration
public class RedisContainerConfig {

    @SuppressWarnings("resource")
    private static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7-alpine").withExposedPorts(6379).withReuse(true);

    static {
        REDIS.start();
        String host = REDIS.getHost();
        String port = String.valueOf(REDIS.getMappedPort(6379));
        System.setProperty("spring.data.redis.host", host);
        System.setProperty("spring.data.redis.port", port);
        log.info("Redis container started at {}:{}", host, port);
    }
}
