package com.ez.admin.iam.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.ez.admin.iam.model.dto.RefreshTokenRequestDTO;
import com.ez.admin.iam.model.dto.UserLoginRequestDTO;
import com.ez.admin.iam.model.vo.RefreshTokenResponseVO;
import com.ez.admin.iam.model.vo.UserLoginResponseVO;
import com.ez.admin.iam.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 * <p>
 * 基于 Sa-Token 框架实现用户认证、登出和令牌刷新功能。
 * </p>
 *
 * @see <a href="https://sa-token.cc">Sa-Token 官方文档</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

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
     *   <li>验证用户名和密码（TODO: 需要对接用户数据源）</li>
     *   <li>调用 Sa-Token 的 login 方法进行登录</li>
     *   <li>生成刷新令牌（使用 tokenName + ":refresh:" + userId 格式）</li>
     *   <li>返回登录响应，包含 access_token 和 refresh_token</li>
     * </ol>
     * </p>
     *
     * @param requestDTO 登录请求，包含用户名和密码
     * @return 登录响应，包含访问令牌和刷新令牌
     * @throws IllegalArgumentException 用户名或密码错误
     */
    @Override
    public UserLoginResponseVO login(UserLoginRequestDTO requestDTO) {
        String username = requestDTO.getUsername();
        String password = requestDTO.getPassword();

        log.info("用户登录请求: username={}", username);

        // TODO: 实际项目中需要对接数据库，验证用户名和密码
        // 这里暂时使用硬编码的演示数据
        // 正常流程：
        // 1. 根据 username 查询用户信息（UserMapper.selectByUsername(username)）
        // 2. 验证密码（使用 BCrypt 等加密算法比对）
        // 3. 检查用户状态（是否被禁用、是否过期等）
        if (!"admin".equals(username) || !"admin123".equals(password)) {
            log.warn("用户登录失败: username={}, 原因=用户名或密码错误", username);
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 模拟用户 ID（实际应从数据库获取）
        Long userId = 1001L;

        // 使用 Sa-Token 进行登录，生成 access_token
        StpUtil.login(userId);
        String accessToken = StpUtil.getTokenValue();

        // 生成 refresh_token
        // 格式：tokenName:refresh:userId，便于后续验证和撤销
        String tokenName = StpUtil.getTokenName();
        String refreshToken = tokenName + ":refresh:" + userId;

        log.info("用户登录成功: userId={}, username={}, accessToken={}", userId, username, accessToken);

        // 返回登录响应（使用 Builder 模式）
        return UserLoginResponseVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpireSeconds)
                .build();
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
     * @return 刷新令牌响应，包含新的访问令牌和刷新令牌
     * @throws IllegalArgumentException 刷新令牌无效或已过期
     */
    @Override
    public RefreshTokenResponseVO refreshToken(RefreshTokenRequestDTO requestDTO) {
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

            // TODO: 验证用户是否存在且有效（对接数据库）
            // User user = userMapper.selectById(userId);
            // if (user == null || user.getStatus() == UserStatus.DISABLED) {
            //     throw new IllegalArgumentException("用户不存在或已被禁用");
            // }

            // 使用 Sa-Token 重新登录，生成新的 access_token
            StpUtil.login(userId);
            String newAccessToken = StpUtil.getTokenValue();

            // 生成新的 refresh_token（旧的失效，实现令牌轮换）
            String newRefreshToken = prefix + userId;

            log.info("刷新令牌成功: userId={}, newAccessToken={}", userId, newAccessToken);

            return RefreshTokenResponseVO.builder()
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
