package com.ez.admin.system.controller;

import com.ez.admin.system.api.dto.UserAuthenticationRequestDTO;
import com.ez.admin.system.api.dto.UserAuthenticationResponseVO;
import com.ez.admin.system.entity.SysUser;
import com.ez.admin.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 * <p>
 * 提供用户相关的 REST API 接口，供 IAM 服务通过 Feign 远程调用。
 * 主要功能包括用户认证等。
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/system/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关接口（供内部服务调用）")
public class UserController {

    private final ISysUserService userService;

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
     * @return 用户认证响应，包含用户基本信息和加密密码
     * @throws IllegalArgumentException 用户不存在或已被禁用
     */
    @PostMapping("/authenticate")
    @Operation(summary = "用户认证", description = "根据用户名查询用户认证信息")
    public UserAuthenticationResponseVO authenticateUser(@Valid @RequestBody UserAuthenticationRequestDTO requestDTO) {
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
        UserAuthenticationResponseVO responseVO = UserAuthenticationResponseVO.builder()
                .userId(sysUser.getUserId())
                .username(sysUser.getUsername())
                .nickname(sysUser.getNickname())
                .status(sysUser.getStatus())
                .password(sysUser.getPassword())
                .build();

        log.info("用户认证成功: username={}, userId={}", username, sysUser.getUserId());

        return responseVO;
    }
}
