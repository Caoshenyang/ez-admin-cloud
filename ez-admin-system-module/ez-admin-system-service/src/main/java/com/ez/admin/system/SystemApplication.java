package com.ez.admin.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 系统模块启动类
 * <p>
 * 提供 RBAC 基础能力（用户、角色、权限、菜单等）。
 * 通过 Feign 暴露 API 供其他服务调用。
 * </p>
 */
@EnableDiscoveryClient
@MapperScan("com.ez.admin.system.mapper")
@SpringBootApplication
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
