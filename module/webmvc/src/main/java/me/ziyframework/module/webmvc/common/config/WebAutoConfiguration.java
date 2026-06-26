package me.ziyframework.module.webmvc.common.config;

import com.google.common.base.Preconditions;
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
                registry.addConverterFactory(new ConverterFactory<String, BaseEnum<?>>() {
                    @Override
                    public <T extends BaseEnum<?>> Converter<String, T> getConverter(Class<T> targetType) {
                        return source -> convertToBaseEnum(Integer.parseInt(source), targetType);
                    }
                });
            }
        };
    }

    /**
     * 将字符串形式的 code 转换为 BaseEnum 枚举实例.
     *
     * <p>{@link ConverterFactory} 的方法签名仅以 {@code BaseEnum<?>} 为上界, 而 {@link BaseEnum#fromCode}
     * 要求传入 {@code Class<E extends Enum<E> & BaseEnum<E>>}, 两者无法在类型系统中精确对齐, 故在此做受控转换.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends BaseEnum<?>> T convertToBaseEnum(int code, Class<T> targetType) {
        T result = (T) BaseEnum.fromCode(code, (Class) targetType);
        return Preconditions.checkNotNull(result, "无效枚举值: %s", code);
    }
}
