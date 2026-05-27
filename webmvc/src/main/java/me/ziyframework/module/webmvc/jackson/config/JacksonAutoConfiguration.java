package me.ziyframework.module.webmvc.jackson.config;

import lombok.RequiredArgsConstructor;
import me.ziyframework.module.webmvc.jackson.module.BaseEnumJacksonModule;
import me.ziyframework.module.webmvc.jackson.module.DesensitizationJacksonModule;
import me.ziyframework.module.webmvc.jackson.module.NullHandlingJacksonModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * Jackson自定义行为配置.<br/>
 * created on 2025-01
 *
 * @author ziy
 */
@AutoConfiguration
@RequiredArgsConstructor
@ComponentScan(basePackages = "me.ziyframework.module.webmvc.jackson")
public class JacksonAutoConfiguration {

    /**
     * 注入长整型自动转换字符串的module.
     */
    @Bean
    public JacksonModule toStringModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        return module;
    }

    /**
     * 支持空对象和空数组的返回.
     */
    @Bean
    public JacksonModule nullModule() {
        return new NullHandlingJacksonModule();
    }

    /**
     * 支持BaseEnum的类序列化.
     */
    @Bean
    public JacksonModule baseEnumModule() {
        return new BaseEnumJacksonModule();
    }

    /**
     * 脱敏序列化模块.
     */
    @Bean
    public JacksonModule desensitizationModule() {
        return new DesensitizationJacksonModule();
    }
}
