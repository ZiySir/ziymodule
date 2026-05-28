package me.ziyframework.module.webmvc.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 通用web module的配置.<br/>
 * created on 2025-04
 * @author ziy
 */
@Data
@ConfigurationProperties(prefix = "module.mvc.common")
public class WebCommonProperties {}
