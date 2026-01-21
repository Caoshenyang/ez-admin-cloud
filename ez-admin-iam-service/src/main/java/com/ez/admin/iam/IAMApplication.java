package com.ez.admin.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * IAM 认证服务启动类
 * <p>
 * 提供用户登录、登出、令牌刷新等认证功能。
 * 通过 OpenFeign 远程调用系统服务获取用户信息。
 * </p>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ez.admin.system.api")
public class IAMApplication {

    public static void main(String[] args) {
        SpringApplication.run(IAMApplication.class, args);
    }
}
