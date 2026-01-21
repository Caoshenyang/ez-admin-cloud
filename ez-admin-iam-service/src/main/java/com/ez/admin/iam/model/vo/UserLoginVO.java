package com.ez.admin.iam.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录 VO
 * <p>
 * 返回用户登录成功后的认证信息，包括访问令牌和刷新令牌。
 * 前端获取令牌后，需要在后续请求的 Header 中携带 Token 进行身份认证。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录")
public class UserLoginVO {

    /**
     * 访问令牌 (Access Token)
     * <p>
     * 用于 API 请求的身份认证，有效期较短（建议 2 小时）。
     * 在请求头中添加：Authorization: Bearer {token}
     * </p>
     */
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    /**
     * 刷新令牌 (Refresh Token)
     * <p>
     * 用于获取新的访问令牌，有效期较长（建议 7 天）。
     * 当 access_token 过期时，使用此令牌调用刷新接口获取新的 token。
     * </p>
     */
    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    /**
     * 令牌类型
     * <p>
     * 固定值为 Bearer，符合 OAuth 2.0 规范。
     * </p>
     */
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType;

    /**
     * 访问令牌过期时间（秒）
     * <p>
     * 表示 access_token 的有效时长，单位为秒。
     * 前端可以根据此时间判断是否需要刷新 token。
     * </p>
     */
    @Schema(description = "访问令牌过期时间（秒）", example = "7200")
    private Long expiresIn;
}
