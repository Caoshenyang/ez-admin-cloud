package com.ez.admin.satoken.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 公共模块自动配置类
 * <p>
 * 扫描并注册 Sa-Token 相关的组件，包括：
 * <ul>
 *   <li>StpInterfaceImpl：权限认证接口实现</li>
 *   <li>GlobalException：全局异常处理器</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-22
 */
@Configuration
@ComponentScan({
        "com.ez.admin.satoken.common.service",
        "com.ez.admin.satoken.common.handler"
})
public class SaTokenCommonConfig {
}
