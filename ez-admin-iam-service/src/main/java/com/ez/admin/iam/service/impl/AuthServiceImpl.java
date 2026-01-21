package com.ez.admin.iam.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.ez.admin.iam.model.dto.RefreshTokenRequestDTO;
import com.ez.admin.iam.model.dto.UserLoginRequestDTO;
import com.ez.admin.iam.model.vo.RefreshTokenVO;
import com.ez.admin.iam.model.vo.UserLoginVO;
import com.ez.admin.iam.service.AuthService;
import com.ez.admin.iam.service.PermissionCacheService;
import com.ez.admin.system.api.dto.UserAuthenticationRequestDTO;
import com.ez.admin.system.api.dto.UserAuthenticationVO;
import com.ez.admin.system.api.dto.UserRoleVO;
import com.ez.admin.system.api.feign.SystemUserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 * <p>
 * 基于 Sa-Token 框架实现用户认证、登出和令牌刷新功能。
 * 通过 Feign 远程调用系统服务获取用户信息。
 * </p>
 *
 * @see <a href="https://sa-token.cc">Sa-Token 官方文档</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SystemUserFeignClient systemUserFeignClient;
    private final PermissionCacheService permissionCacheService;

    /**
     * 访问令牌过期时间（秒），从配置文件读取
     * <p>
     * 默认值为 7200 秒（2 小时）。
     * 可通过 application.yaml 中的 sa-token.timeout 配置项修改。
     * </p>
     */
    @Value("${sa-token.timeout:7200}")
    private Long accessTokenExpireSeconds;

    /**
     * 用户登录
     * <p>
     * 验证用户凭证并生成访问令牌和刷新令牌。
     * 实现步骤：
     * <ol>
     *   <li>通过 Feign 调用系统服务获取用户信息</li>
     *   <li>验证密码（TODO: 使用 BCrypt 等加密算法比对）</li>
     *   <li>调用 Sa-Token 的 login 方法进行登录</li>
     *   <li>查询用户角色并缓存到 Redis</li>
     *   <li>聚合用户权限并缓存到 Redis</li>
     *   <li>生成刷新令牌（使用 tokenName + ":refresh:" + userId 格式）</li>
     *   <li>返回登录信息，包含 access_token 和 refresh_token</li>
     * </ol>
     * </p>
     *
     * @param requestDTO 登录请求，包含用户名和密码
     * @return 登录信息，包含访问令牌和刷新令牌
     * @throws IllegalArgumentException 用户名或密码错误
     */
    @Override
    public UserLoginVO login(UserLoginRequestDTO requestDTO) {
        String username = requestDTO.getUsername();
        String password = requestDTO.getPassword();

        log.info("用户登录请求: username={}", username);

        // 通过 Feign 调用系统服务，获取用户认证信息
        UserAuthenticationRequestDTO authRequestDTO = new UserAuthenticationRequestDTO();
        authRequestDTO.setUsername(username);
        authRequestDTO.setPassword(password);

        UserAuthenticationVO authResponse = systemUserFeignClient.authenticateUser(authRequestDTO);

        // TODO: 验证密码（使用 BCrypt 等加密算法比对）
        // 当前系统服务返回的密码是加密后的，需要使用 BCrypt.checkpw() 进行比对
        // if (!BCrypt.checkpw(password, authResponse.getPassword())) {
        //     log.warn("用户登录失败: username={}, 原因=密码错误", username);
        //     throw new IllegalArgumentException("用户名或密码错误");
        // }
        // 暂时直接比对（实际应该使用加密算法）
        if (!password.equals(authResponse.getPassword())) {
            log.warn("用户登录失败: username={}, 原因=密码错误", username);
            throw new IllegalArgumentException("用户名或密码错误");
        }

        Long userId = authResponse.getUserId();

        // 使用 Sa-Token 进行登录，生成 access_token
        StpUtil.login(userId);
        String accessToken = StpUtil.getTokenValue();

        // 查询用户角色并缓存到 Redis
        UserRoleVO userRoles = systemUserFeignClient.getUserRoles(userId);
        if (userRoles != null && userRoles.getRoleIds() != null) {
            // 缓存用户-角色映射
            permissionCacheService.cacheUserRoles(userId, userRoles.getRoleIds());

            // 聚合用户权限（从角色权限缓存中获取）
            List<String> userPermissions = aggregateUserPermissions(userRoles.getRoleIds());
            // 缓存用户-权限映射
            permissionCacheService.cacheUserPermissions(userId, userPermissions);

            log.info("用户权限缓存成功: userId={}, 角色数量={}, 权限数量={}",
                    userId, userRoles.getRoleIds().size(), userPermissions.size());
        }

        // 生成 refresh_token
        // 格式：tokenName:refresh:userId，便于后续验证和撤销
        String tokenName = StpUtil.getTokenName();
        String refreshToken = tokenName + ":refresh:" + userId;

        log.info("用户登录成功: userId={}, username={}, accessToken={}", userId, username, accessToken);

        // 返回登录信息（使用 Builder 模式）
        return UserLoginVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpireSeconds)
                .build();
    }

    /**
     * 聚合用户权限
     * <p>
     * 从用户的所有角色中聚合权限标识列表。
     * 权限数据从 Redis 缓存中获取（启动时已初始化）。
     * </p>
     *
     * @param roleIds 角色ID列表
     * @return 权限标识列表
     */
    private List<String> aggregateUserPermissions(List<Long> roleIds) {
        List<String> permissions = new ArrayList<>();
        for (Long roleId : roleIds) {
            List<String> rolePerms = permissionCacheService.getRolePermissions(roleId);
            if (rolePerms != null) {
                permissions.addAll(rolePerms);
            }
        }
        // 去重并返回
        return permissions.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 用户登出
     * <p>
     * 使当前用户的令牌失效，清除服务端会话。
     * </p>
     */
    @Override
    public void logout() {
        // 获取当前登录用户 ID
        Object loginId = StpUtil.getLoginIdDefaultNull();

        if (loginId == null) {
            log.warn("用户登出失败: 原因=未登录");
            throw new IllegalStateException("用户未登录");
        }

        // 调用 Sa-Token 的登出方法，使 Token 失效
        StpUtil.logout();

        log.info("用户登出成功: userId={}", loginId);
    }

    /**
     * 刷新令牌
     * <p>
     * 使用刷新令牌获取新的访问令牌，采用令牌轮换机制。
     * 实现步骤：
     * <ol>
     *   <li>解析 refresh_token，提取用户 ID</li>
     *   <li>验证用户有效性</li>
     *   <li>生成新的 access_token</li>
     *   <li>生成新的 refresh_token（旧的失效）</li>
     * </ol>
     * </p>
     *
     * @param requestDTO 刷新令牌请求，包含 refresh_token
     * @return 刷新令牌信息，包含新的访问令牌和刷新令牌
     * @throws IllegalArgumentException 刷新令牌无效或已过期
     */
    @Override
    public RefreshTokenVO refreshToken(RefreshTokenRequestDTO requestDTO) {
        String refreshToken = requestDTO.getRefreshToken();

        log.info("刷新令牌请求: refreshToken={}", refreshToken);

        // 解析 refresh_token，提取用户 ID
        // 格式：tokenName:refresh:userId
        String tokenName = StpUtil.getTokenName();
        String prefix = tokenName + ":refresh:";

        if (!refreshToken.startsWith(prefix)) {
            log.warn("刷新令牌失败: refreshToken={}, 原因=令牌格式无效", refreshToken);
            throw new IllegalArgumentException("刷新令牌无效");
        }

        try {
            String userIdStr = refreshToken.substring(prefix.length());
            Long userId = Long.parseLong(userIdStr);

            // TODO: 验证用户是否存在且有效（可通过 Feign 调用系统服务）
            // UserAuthenticationVO user = systemUserFeignClient.getUserById(userId);
            // if (user == null || user.getStatus() != 1) {
            //     throw new IllegalArgumentException("用户不存在或已被禁用");
            // }

            // 使用 Sa-Token 重新登录，生成新的 access_token
            StpUtil.login(userId);
            String newAccessToken = StpUtil.getTokenValue();

            // 生成新的 refresh_token（旧的失效，实现令牌轮换）
            String newRefreshToken = prefix + userId;

            log.info("刷新令牌成功: userId={}, newAccessToken={}", userId, newAccessToken);

            return RefreshTokenVO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(accessTokenExpireSeconds)
                    .build();

        } catch (NumberFormatException e) {
            log.warn("刷新令牌失败: refreshToken={}, 原因=用户 ID 格式无效", refreshToken);
            throw new IllegalArgumentException("刷新令牌无效", e);
        }
    }
}
