package com.ez.admin.iam.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 刷新令牌 VO
 * <p>
 * 返回刷新后的认证信息，包含新的访问令牌和刷新令牌。
 * 为了安全性，刷新令牌也应该轮换更新（旧的 refresh_token 失效）。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "刷新令牌")
public class RefreshTokenVO {

    /**
     * 新的访问令牌 (Access Token)
     * <p>
     * 刷新后获得的新的访问令牌，有效期重置。
     * 前端需要更新本地存储的 access_token。
     * </p>
     */
    @Schema(description = "新的访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    /**
     * 新的刷新令牌 (Refresh Token)
     * <p>
     * 刷新后获得的新的刷新令牌，旧的 refresh_token 同时失效。
     * 这种机制称为"令牌轮换"，可以降低令牌泄露的安全风险。
     * </p>
     */
    @Schema(description = "新的刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
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
     * 表示新的 access_token 的有效时长，单位为秒。
     * </p>
     */
    @Schema(description = "访问令牌过期时间（秒）", example = "7200")
    private Long expiresIn;
}
