package com.ez.admin.iam.controller;

import com.ez.admin.iam.model.dto.RefreshTokenRequestDTO;
import com.ez.admin.iam.model.dto.UserLoginRequestDTO;
import com.ez.admin.iam.model.vo.RefreshTokenResponseVO;
import com.ez.admin.iam.model.vo.UserLoginResponseVO;
import com.ez.admin.iam.service.AuthService;
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


    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录，返回访问令牌和刷新令牌")
    public UserLoginResponseVO login(@Valid @RequestBody UserLoginRequestDTO requestDTO) {
        log.info("收到登录请求: username={}", requestDTO.getUsername());
        return authService.login(requestDTO);
    }


    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "登出当前用户，使访问令牌失效")
    public void logout() {
        log.info("收到登出请求");
        authService.logout();
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌，采用令牌轮换机制")
    public RefreshTokenResponseVO refreshToken(@Valid @RequestBody RefreshTokenRequestDTO requestDTO) {
        log.info("收到刷新令牌请求");
        return authService.refreshToken(requestDTO);
    }
}
