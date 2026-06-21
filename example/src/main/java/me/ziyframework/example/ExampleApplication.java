package me.ziyframework.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * .
 * created in 2026-05
 * @author ziy
 */
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication
@EntityScan(basePackages = "me.ziyframework.module.security.entity")
@EnableJpaRepositories(basePackages = "me.ziyframework.module.security.entity")
public class ExampleApplication {

    static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
