package com.ez.admin.iam.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新令牌请求 DTO
 * <p>
 * 用于在访问令牌过期后，使用刷新令牌获取新的访问令牌。
 * 这样可以避免用户频繁重新登录，提升用户体验。
 * </p>
 */
@Data
@Schema(description = "刷新令牌请求")
public class RefreshTokenRequestDTO {

    /**
     * 刷新令牌
     * <p>
     * 用户登录时获得的长期有效令牌，用于获取新的访问令牌。
     * 不允许为空，且必须是有效且未过期的 refresh_token。
     * </p>
     */
    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}
