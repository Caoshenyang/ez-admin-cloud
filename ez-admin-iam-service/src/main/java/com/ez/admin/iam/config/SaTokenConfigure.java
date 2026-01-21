package com.ez.admin.iam.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 配置类
 * <p>
 * 配置 Sa-Token 框架的相关参数，包括 JWT 认证逻辑。
 * </p>
 */
@Configuration
public class SaTokenConfigure {

    /**
     * 配置 Sa-Token 框架的 JWT 认证逻辑
     * <p>
     * 使用 {@link StpLogicJwtForSimple} 实现基于 JWT 的简单认证逻辑。
     * </p>
     *
     * @return StpLogicJwtForSimple 实例
     */
    @Bean
    public StpLogic stpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
