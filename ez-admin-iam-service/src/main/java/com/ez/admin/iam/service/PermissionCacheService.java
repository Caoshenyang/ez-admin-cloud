package com.ez.admin.iam.service;

import java.util.List;

/**
 * 权限缓存服务接口
 * <p>
 * 提供角色-权限、用户-角色、用户-权限的缓存操作。
 * 缓存存储在 Redis 中，用于提升权限验证性能。
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
public interface PermissionCacheService {

    /**
     * 缓存角色-权限映射
     * <p>
     * Redis Key 格式: iam:role:perms:{roleId}
     * Redis Value 格式: JSON 数组，包含权限标识列表
     * </p>
     *
     * @param roleId   角色ID
     * @param permissions 权限标识列表
     */
    void cacheRolePermissions(Long roleId, List<String> permissions);

    /**
     * 获取角色的权限列表
     * <p>
     * 从 Redis 中获取角色对应的权限标识列表。
     * </p>
     *
     * @param roleId 角色ID
     * @return 权限标识列表，如果不存在则返回 null
     */
    List<String> getRolePermissions(Long roleId);

    /**
     * 缓存用户-角色映射
     * <p>
     * Redis Key 格式: iam:user:roles:{userId}
     * Redis Value 格式: JSON 数组，包含角色ID列表
     * </p>
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void cacheUserRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户的角色列表
     * <p>
     * 从 Redis 中获取用户对应的角色ID列表。
     * </p>
     *
     * @param userId 用户ID
     * @return 角色ID列表，如果不存在则返回 null
     */
    List<Long> getUserRoles(Long userId);

    /**
     * 缓存用户-权限映射
     * <p>
     * Redis Key 格式: iam:user:perms:{userId}
     * Redis Value 格式: JSON 数组，包含用户所有权限标识
     * </p>
     *
     * @param userId      用户ID
     * @param permissions 权限标识列表
     */
    void cacheUserPermissions(Long userId, List<String> permissions);

    /**
     * 获取用户的权限列表
     * <p>
     * 从 Redis 中获取用户的所有权限标识列表。
     * </p>
     *
     * @param userId 用户ID
     * @return 权限标识列表，如果不存在则返回 null
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 删除角色权限缓存
     * <p>
     * 当角色或菜单数据变更时调用。
     * </p>
     *
     * @param roleId 角色ID
     */
    void evictRolePermissions(Long roleId);

    /**
     * 删除用户角色和权限缓存
     * <p>
     * 当用户角色关系变更时调用。
     * </p>
     *
     * @param userId 用户ID
     */
    void evictUserCache(Long userId);

    /**
     * 刷新所有角色权限缓存
     * <p>
     * 从数据库重新加载所有角色权限并更新缓存。
     * </p>
     */
    void refreshAllRolePermissions();
}
