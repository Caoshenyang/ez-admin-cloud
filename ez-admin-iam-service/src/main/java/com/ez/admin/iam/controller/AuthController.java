package com.ez.admin.iam.controller;

import com.ez.admin.iam.model.dto.RefreshTokenRequestDTO;
import com.ez.admin.iam.model.dto.UserLoginRequestDTO;
import com.ez.admin.iam.model.vo.RefreshTokenVO;
import com.ez.admin.iam.model.vo.UserLoginVO;
import com.ez.admin.iam.service.AuthService;
import com.ez.admin.core.entity.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * <p>
 * 提供用户认证相关的 REST API 接口，包括登录、登出和令牌刷新功能。
 * 基于 Sa-Token 框架实现认证和授权管理。
 * 所有接口统一返回 {@link R} 格式。
 * </p>
 *
 * @see <a href="https://sa-token.cc">Sa-Token 官方文档</a>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、登出、令牌刷新等认证相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     * <p>
     * 验证用户凭证（用户名和密码），验证通过后生成访问令牌和刷新令牌。
     * </p>
     *
     * @param requestDTO 登录请求，包含用户名和密码
     * @return 登录信息，包含访问令牌和刷新令牌
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录，返回访问令牌和刷新令牌")
    public R<UserLoginVO> login(@Valid @RequestBody UserLoginRequestDTO requestDTO) {
        log.info("收到登录请求: username={}", requestDTO.getUsername());
        UserLoginVO result = authService.login(requestDTO);
        return R.ok(result);
    }

    /**
     * 用户登出
     * <p>
     * 使当前用户的令牌失效，清除服务端的会话信息。
     * </p>
     *
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "登出当前用户，使访问令牌失效")
    public R<Void> logout() {
        log.info("收到登出请求");
        authService.logout();
        return R.ok("登出成功", null);
    }

    /**
     * 刷新令牌
     * <p>
     * 当访问令牌过期时，使用刷新令牌获取新的访问令牌。
     * 为了安全性，采用令牌轮换机制：刷新成功后，旧的 refresh_token 失效。
     * </p>
     *
     * @param requestDTO 刷新令牌请求，包含有效的 refresh_token
     * @return 刷新令牌信息，包含新的访问令牌和刷新令牌
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌，采用令牌轮换机制")
    public R<RefreshTokenVO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO requestDTO) {
        log.info("收到刷新令牌请求");
        RefreshTokenVO result = authService.refreshToken(requestDTO);
        return R.ok(result);
    }
}
