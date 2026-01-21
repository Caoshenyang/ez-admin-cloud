package com.ez.admin.iam.service;

import com.ez.admin.iam.model.dto.RefreshTokenRequestDTO;
import com.ez.admin.iam.model.dto.UserLoginRequestDTO;
import com.ez.admin.iam.model.vo.RefreshTokenVO;
import com.ez.admin.iam.model.vo.UserLoginVO;

/**
 * 认证服务接口
 * <p>
 * 提供用户认证相关的核心业务逻辑，包括登录、登出、令牌刷新等功能。
 * 该接口基于 Sa-Token 框架实现认证和授权管理。
 * </p>
 *
 * @see <a href="https://sa-token.cc">Sa-Token 官方文档</a>
 */
public interface AuthService {

    /**
     * 用户登录
     * <p>
     * 验证用户凭证（用户名和密码），验证通过后生成访问令牌和刷新令牌。
     * 核心业务逻辑：
     * <ol>
     *   <li>查询用户信息（用户名是否存在）</li>
     *   <li>验证密码是否正确（使用加密算法比对）</li>
     *   <li>检查用户状态（是否被禁用）</li>
     *   <li>生成 Sa-Token 的 Token（作为 access_token）</li>
     *   <li>生成刷新令牌（用于后续续期）</li>
     *   <li>记录登录日志（IP、时间、设备等信息）</li>
     * </ol>
     * </p>
     *
     * @param requestDTO 登录请求，包含用户名和密码
     * @return 登录信息，包含访问令牌和刷新令牌
     * @throws IllegalArgumentException 用户名或密码错误
     * @throws IllegalStateException    用户已被禁用
     */
    UserLoginVO login(UserLoginRequestDTO requestDTO);

    /**
     * 用户登出
     * <p>
     * 使当前用户的令牌失效，清除服务端的会话信息。
     * 核心业务逻辑：
     * <ol>
     *   <li>从请求上下文中获取当前登录用户的 Token</li>
     *   <li>调用 Sa-Token 的踢出登录方法，使 Token 失效</li>
     *   <li>清除用户缓存信息（如权限缓存）</li>
     *   <li>记录登出日志</li>
     * </ol>
     * </p>
     */
    void logout();

    /**
     * 刷新令牌
     * <p>
     * 当访问令牌过期时，使用刷新令牌获取新的访问令牌。
     * 为了安全性，采用令牌轮换机制：刷新成功后，旧的 refresh_token 失效。
     * 核心业务逻辑：
     * <ol>
     *   <li>验证 refresh_token 的有效性（是否过期、是否被撤销）</li>
     *   <li>从 refresh_token 中提取用户标识</li>
     *   <li>生成新的 access_token 和 refresh_token</li>
     *   <li>使旧的 refresh_token 失效（令牌轮换）</li>
     * </ol>
     * </p>
     *
     * @param requestDTO 刷新令牌请求，包含有效的 refresh_token
     * @return 刷新令牌信息，包含新的访问令牌和刷新令牌
     * @throws IllegalArgumentException 刷新令牌无效或已过期
     */
    RefreshTokenVO refreshToken(RefreshTokenRequestDTO requestDTO);
}
