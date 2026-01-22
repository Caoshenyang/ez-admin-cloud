package com.ez.admin.system.controller;

import com.ez.admin.core.entity.R;
import com.ez.admin.system.api.dto.UserAuthenticationRequestDTO;
import com.ez.admin.system.api.dto.UserAuthenticationVO;
import com.ez.admin.system.api.dto.UserRoleVO;
import com.ez.admin.system.entity.SysRole;
import com.ez.admin.system.entity.SysUser;
import com.ez.admin.system.service.SysRoleService;
import com.ez.admin.system.service.SysUserRoleRelationService;
import com.ez.admin.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户控制器
 * <p>
 * 提供用户相关的 REST API 接口，供 IAM 服务通过 Feign 远程调用。
 * 主要功能包括用户认证、用户角色查询等。
 * 所有接口统一返回 {@link R} 格式。
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/system/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关接口（供内部服务调用）")
public class UserController {

    private final SysUserService userService;
    private final SysUserRoleRelationService userRoleRelationService;
    private final SysRoleService roleService;

    /**
     * 用户认证接口
     * <p>
     * 此接口供 IAM 服务在用户登录时调用，用于验证用户身份。
     * 实现步骤：
     * <ol>
     *   <li>根据用户名查询用户信息</li>
     *   <li>检查用户是否存在</li>
     *   <li>检查用户状态是否正常</li>
     *   <li>返回用户认证信息（包含加密后的密码）</li>
     * </ol>
     * </p>
     *
     * @param requestDTO 用户认证请求，包含用户名和密码
     * @return 用户认证信息，包含用户基本信息和加密密码
     * @throws IllegalArgumentException 用户不存在或已被禁用
     */
    @PostMapping("/authenticate")
    @Operation(summary = "用户认证", description = "根据用户名查询用户认证信息")
    public R<UserAuthenticationVO> authenticateUser(@Valid @RequestBody UserAuthenticationRequestDTO requestDTO) {
        String username = requestDTO.getUsername();
        log.info("收到用户认证请求: username={}", username);

        // 根据用户名查询用户信息
        SysUser sysUser = userService.getUserByUsername(username);

        // 检查用户是否存在
        if (sysUser == null) {
            log.warn("用户认证失败: username={}, 原因=用户不存在", username);
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 检查用户状态是否正常（0-禁用，1-正常）
        if (sysUser.getStatus() == null || sysUser.getStatus() != 1) {
            log.warn("用户认证失败: username={}, userId={}, 原因=用户已被禁用", username, sysUser.getUserId());
            throw new IllegalArgumentException("用户已被禁用");
        }

        // 转换为响应对象
        UserAuthenticationVO responseVO = UserAuthenticationVO.builder()
                .userId(sysUser.getUserId())
                .username(sysUser.getUsername())
                .nickname(sysUser.getNickname())
                .status(sysUser.getStatus())
                .password(sysUser.getPassword())
                .build();

        log.info("用户认证成功: username={}, userId={}", username, sysUser.getUserId());

        return R.ok(responseVO);
    }

    /**
     * 查询用户角色信息
     * <p>
     * 此接口供 IAM 服务在用户登录时调用，用于缓存用户-角色关系。
     * </p>
     *
     * @param userId 用户ID
     * @return 用户角色信息，包含用户关联的角色ID和角色标识列表
     */
    @GetMapping("/roles")
    @Operation(summary = "查询用户角色", description = "根据用户ID查询用户关联的角色列表")
    public R<UserRoleVO> getUserRoles(
            @Parameter(description = "用户ID", required = true)
            @RequestParam("userId") Long userId) {
        log.info("收到查询用户角色请求: userId={}", userId);

        // 查询用户关联的角色ID列表
        List<Long> roleIds = userRoleRelationService.getRoleIdsByUserId(userId);

        // 根据角色ID列表查询角色信息，获取角色标识
        List<String> roleLabels = List.of();
        if (!roleIds.isEmpty()) {
            roleLabels = roleService.listByIds(roleIds).stream()
                    .map(SysRole::getRoleLabel)
                    .collect(Collectors.toList());
        }

        UserRoleVO responseVO = UserRoleVO.builder()
                .userId(userId)
                .roleIds(roleIds)
                .roleLabels(roleLabels)
                .build();

        log.info("查询用户角色成功: userId={}, 角色数量={}", userId, roleIds.size());

        return R.ok(responseVO);
    }
}
