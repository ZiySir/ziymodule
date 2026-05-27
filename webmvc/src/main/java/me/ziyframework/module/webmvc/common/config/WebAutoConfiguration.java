package me.ziyframework.module.webmvc.common.config;

import lombok.RequiredArgsConstructor;
import me.ziyframework.framework.enumeration.BaseEnum;
import me.ziyframework.module.webmvc.common.exception.ExceptionAdvice;
import me.ziyframework.module.webmvc.common.mapping.CustomRequestMappingHandlerMapping;
import me.ziyframework.module.webmvc.common.mapping.RequestMappingInfoConsumer;
import me.ziyframework.module.webmvc.common.mapping.RequestMappingInfoResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * web自动配置.
 *
 * @author ziy
 */
@AutoConfiguration
@EnableAspectJAutoProxy
@RequiredArgsConstructor
@EnableConfigurationProperties(WebCommonProperties.class)
@ComponentScan(basePackageClasses = CustomRequestMappingHandlerMapping.class)
public class WebAutoConfiguration {

    /**
     * 全局异常处理.
     */
    @Bean
    public ExceptionAdvice globalExceptionAdvice() {
        return new ExceptionAdvice();
    }

    /**
     * 注册自定义RequestMappingHandlerMapping.
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Primary
    public WebMvcRegistrations webMvcRegistrations(
            ObjectProvider<RequestMappingInfoResolver> resolvers,
            ObjectProvider<RequestMappingInfoConsumer> consumers) {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new CustomRequestMappingHandlerMapping(
                        resolvers.orderedStream().toList(),
                        consumers.orderedStream().toList());
            }
        };
    }

    /**
     * 注册转换器.
     */
    @Bean
    public WebMvcConfigurer converterConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addFormatters(FormatterRegistry registry) {
                registry.addConverterFactory(new ConverterFactory<String, BaseEnum>() {
                    @Override
                    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
                        return source -> BaseEnum.fromCode(Integer.parseInt(source), targetType);
                    }
                });
            }
        };
    }
}
