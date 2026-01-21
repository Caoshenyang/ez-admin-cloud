package com.ez.admin.iam.controller;

import com.ez.admin.iam.service.PermissionCacheService;
import com.ez.admin.result.entity.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 权限缓存控制器
 * <p>
 * 提供权限缓存刷新和清理的 REST API 接口。
 * 用于在数据库变更后手动刷新缓存。
 * 所有接口统一返回 {@link R} 格式。
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/iam/cache")
@RequiredArgsConstructor
@Tag(name = "权限缓存管理", description = "权限缓存刷新和清理接口")
public class PermissionCacheController {

    private final PermissionCacheService permissionCacheService;

    /**
     * 刷新所有角色权限缓存
     * <p>
     * 从数据库重新加载所有角色权限并更新缓存。
     * 适用场景：角色或菜单数据变更后，需要刷新缓存。
     * </p>
     *
     * @return 操作结果
     */
    @PostMapping("/role-permissions/refresh")
    @Operation(summary = "刷新所有角色权限缓存", description = "从数据库重新加载所有角色权限并更新缓存")
    public R<Void> refreshAllRolePermissions() {
        log.info("收到刷新所有角色权限缓存请求");
        permissionCacheService.refreshAllRolePermissions();
        return R.ok("角色权限缓存刷新已触发", null);
    }

    /**
     * 删除指定角色的权限缓存
     * <p>
     * 适用场景：单个角色或菜单数据变更后，需要删除对应缓存。
     * </p>
     *
     * @param roleId 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/role-permissions/{roleId}")
    @Operation(summary = "删除角色权限缓存", description = "删除指定角色的权限缓存")
    public R<Void> evictRolePermissions(
            @Parameter(description = "角色ID", required = true)
            @PathVariable("roleId") Long roleId) {
        log.info("收到删除角色权限缓存请求: roleId={}", roleId);
        permissionCacheService.evictRolePermissions(roleId);
        return R.ok("角色权限缓存已删除", null);
    }

    /**
     * 删除指定用户的缓存
     * <p>
     * 适用场景：用户角色关系变更后，需要删除对应缓存。
     * 下次用户登录时会自动重新加载缓存。
     * </p>
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "删除用户缓存", description = "删除指定用户的角色和权限缓存")
    public R<Void> evictUserCache(
            @Parameter(description = "用户ID", required = true)
            @PathVariable("userId") Long userId) {
        log.info("收到删除用户缓存请求: userId={}", userId);
        permissionCacheService.evictUserCache(userId);
        return R.ok("用户缓存已删除", null);
    }
}
