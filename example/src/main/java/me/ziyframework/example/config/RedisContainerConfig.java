package me.ziyframework.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;

/** 通过 TestContainer 启动 Redis 并把 host/port 注入到系统属性. */
@Slf4j
@AutoConfiguration
public class RedisContainerConfig {

    /**
     * redis container.
     */
    @Bean
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);
    }
}
