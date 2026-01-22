package com.ez.admin.iam.redis;

import com.ez.admin.system.api.vo.RolePermissionVO;
import com.ez.admin.system.api.feign.SystemUserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 权限缓存服务类
 * <p>
 * 使用 Redis 存储角色-权限、用户-角色、用户-权限的映射关系。
 * </p>
 * <p>
 * RedisTemplate 使用全局 Jackson 配置：
 * <ul>
 *   <li>时间格式：yyyy-MM-dd HH:mm:ss</li>
 *   <li>Long 类型：序列化为字符串（避免前端精度丢失）</li>
 *   <li>类型安全：配置白名单，防止反序列化攻击</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    /**
     * Redis Key 前缀常量
     */
    private static final String ROLE_PERMISSIONS_KEY_PREFIX = "iam:role:perms:";
    private static final String USER_ROLES_KEY_PREFIX = "iam:user:roles:";
    private static final String USER_PERMISSIONS_KEY_PREFIX = "iam:user:perms:";

    /**
     * 缓存过期时间（永久有效，直到主动刷新或删除）
     */
    private static final Long CACHE_EXPIRE_SECONDS = null;

    private final RedisTemplate<String, Object> redisTemplate;
    private final SystemUserFeignClient systemUserFeignClient;

    public void cacheRolePermissions(Long roleId, List<String> permissions) {
        String key = ROLE_PERMISSIONS_KEY_PREFIX + roleId;
        try {
            redisTemplate.opsForValue().set(key, permissions);
            if (CACHE_EXPIRE_SECONDS != null) {
                redisTemplate.expire(key, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            }
            log.debug("缓存角色权限: roleId={}, 权限数量={}", roleId, permissions.size());
        } catch (Exception e) {
            log.error("缓存角色权限失败: roleId={}", roleId, e);
        }
    }

    public List<String> getRolePermissions(Long roleId) {
        String key = ROLE_PERMISSIONS_KEY_PREFIX + roleId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.debug("角色权限缓存未命中: roleId={}", roleId);
                return null;
            }
            @SuppressWarnings("unchecked")
            List<String> permissions = (List<String>) value;
            log.debug("获取角色权限缓存: roleId={}, 权限数量={}", roleId, permissions.size());
            return permissions;
        } catch (Exception e) {
            log.error("获取角色权限缓存失败: roleId={}", roleId, e);
            return null;
        }
    }

    public void cacheUserRoles(Long userId, List<Long> roleIds) {
        String key = USER_ROLES_KEY_PREFIX + userId;
        try {
            redisTemplate.opsForValue().set(key, roleIds);
            if (CACHE_EXPIRE_SECONDS != null) {
                redisTemplate.expire(key, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            }
            log.debug("缓存用户角色: userId={}, 角色数量={}", userId, roleIds.size());
        } catch (Exception e) {
            log.error("缓存用户角色失败: userId={}", userId, e);
        }
    }

    public List<Long> getUserRoles(Long userId) {
        String key = USER_ROLES_KEY_PREFIX + userId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.debug("用户角色缓存未命中: userId={}", userId);
                return null;
            }
            @SuppressWarnings("unchecked")
            List<Long> roleIds = (List<Long>) value;
            log.debug("获取用户角色缓存: userId={}, 角色数量={}", userId, roleIds.size());
            return roleIds;
        } catch (Exception e) {
            log.error("获取用户角色缓存失败: userId={}", userId, e);
            return null;
        }
    }

    public void cacheUserPermissions(Long userId, List<String> permissions) {
        String key = USER_PERMISSIONS_KEY_PREFIX + userId;
        try {
            redisTemplate.opsForValue().set(key, permissions);
            if (CACHE_EXPIRE_SECONDS != null) {
                redisTemplate.expire(key, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            }
            log.debug("缓存用户权限: userId={}, 权限数量={}", userId, permissions.size());
        } catch (Exception e) {
            log.error("缓存用户权限失败: userId={}", userId, e);
        }
    }

    public List<String> getUserPermissions(Long userId) {
        String key = USER_PERMISSIONS_KEY_PREFIX + userId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.debug("用户权限缓存未命中: userId={}", userId);
                return null;
            }
            @SuppressWarnings("unchecked")
            List<String> permissions = (List<String>) value;
            log.debug("获取用户权限缓存: userId={}, 权限数量={}", userId, permissions.size());
            return permissions;
        } catch (Exception e) {
            log.error("获取用户权限缓存失败: userId={}", userId, e);
            return null;
        }
    }

    public void evictRolePermissions(Long roleId) {
        String key = ROLE_PERMISSIONS_KEY_PREFIX + roleId;
        try {
            redisTemplate.delete(key);
            log.info("删除角色权限缓存: roleId={}", roleId);
        } catch (Exception e) {
            log.error("删除角色权限缓存失败: roleId={}", roleId, e);
        }
    }

    public void evictUserCache(Long userId) {
        String rolesKey = USER_ROLES_KEY_PREFIX + userId;
        String permsKey = USER_PERMISSIONS_KEY_PREFIX + userId;
        try {
            redisTemplate.delete(rolesKey);
            redisTemplate.delete(permsKey);
            log.info("删除用户缓存: userId={}", userId);
        } catch (Exception e) {
            log.error("删除用户缓存失败: userId={}", userId, e);
        }
    }

    public void refreshAllRolePermissions() {
        log.info("========================================");
        log.info("开始刷新所有角色权限缓存...");
        log.info("========================================");

        try {
            // 通过 Feign 调用系统服务，获取所有角色权限
            List<RolePermissionVO> rolePermissions = systemUserFeignClient.getAllRolePermissions();

            if (rolePermissions == null || rolePermissions.isEmpty()) {
                log.warn("未获取到任何角色权限数据，缓存刷新跳过");
                return;
            }

            // 遍历角色权限列表，逐个缓存到 Redis
            int successCount = 0;
            int failCount = 0;

            for (RolePermissionVO rolePermission : rolePermissions) {
                try {
                    cacheRolePermissions(
                            rolePermission.getRoleId(),
                            rolePermission.getPermissions()
                    );
                    successCount++;
                    log.debug("刷新角色权限缓存成功: roleId={}, roleLabel={}, 权限数量={}",
                            rolePermission.getRoleId(),
                            rolePermission.getRoleLabel(),
                            rolePermission.getPermissions() != null ? rolePermission.getPermissions().size() : 0);
                } catch (Exception e) {
                    failCount++;
                    log.error("刷新角色权限缓存失败: roleId={}, roleLabel={}",
                            rolePermission.getRoleId(), rolePermission.getRoleLabel(), e);
                }
            }

            log.info("========================================");
            log.info("角色权限缓存刷新完成: 成功={}, 失败={}, 总计={}",
                    successCount, failCount, rolePermissions.size());
            log.info("========================================");

        } catch (Exception e) {
            log.error("角色权限缓存刷新失败", e);
        }
    }
}
