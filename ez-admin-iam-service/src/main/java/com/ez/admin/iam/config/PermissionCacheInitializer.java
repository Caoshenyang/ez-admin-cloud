package com.ez.admin.iam.config;

import com.ez.admin.iam.service.PermissionCacheService;
import com.ez.admin.system.api.dto.RolePermissionVO;
import com.ez.admin.system.api.feign.SystemUserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限缓存初始化器
 * <p>
 * 在应用启动时，通过 Feign 调用系统服务获取所有角色及其权限列表，
 * 并将其缓存到 Redis 中。
 * </p>
 * <p>
 * 缓存结构：
 * <ul>
 *   <li>Key: iam:role:perms:{roleId}</li>
 *   <li>Value: JSON 数组，包含权限标识列表</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionCacheInitializer implements ApplicationRunner {

    private final PermissionCacheService permissionCacheService;
    private final SystemUserFeignClient systemUserFeignClient;

    /**
     * 应用启动后执行
     * <p>
     * 加载所有角色权限并缓存到 Redis。
     * </p>
     *
     * @param args 应用启动参数
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("========================================");
        log.info("开始初始化角色权限缓存...");
        log.info("========================================");

        try {
            // 通过 Feign 调用系统服务，获取所有角色权限
            List<RolePermissionVO> rolePermissions = systemUserFeignClient.getAllRolePermissions();

            if (rolePermissions == null || rolePermissions.isEmpty()) {
                log.warn("未获取到任何角色权限数据，缓存初始化跳过");
                return;
            }

            // 遍历角色权限列表，逐个缓存到 Redis
            int successCount = 0;
            int failCount = 0;

            for (RolePermissionVO rolePermission : rolePermissions) {
                try {
                    permissionCacheService.cacheRolePermissions(
                            rolePermission.getRoleId(),
                            rolePermission.getPermissions()
                    );
                    successCount++;
                    log.debug("缓存角色权限成功: roleId={}, roleLabel={}, 权限数量={}",
                            rolePermission.getRoleId(),
                            rolePermission.getRoleLabel(),
                            rolePermission.getPermissions() != null ? rolePermission.getPermissions().size() : 0);
                } catch (Exception e) {
                    failCount++;
                    log.error("缓存角色权限失败: roleId={}, roleLabel={}",
                            rolePermission.getRoleId(), rolePermission.getRoleLabel(), e);
                }
            }

            log.info("========================================");
            log.info("角色权限缓存初始化完成: 成功={}, 失败={}, 总计={}",
                    successCount, failCount, rolePermissions.size());
            log.info("========================================");

        } catch (Exception e) {
            log.error("角色权限缓存初始化失败", e);
        }
    }
}
