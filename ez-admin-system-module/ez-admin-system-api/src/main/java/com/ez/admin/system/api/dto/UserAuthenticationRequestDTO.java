package com.ez.admin.system.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户认证请求 DTO
 * <p>
 * 用于 IAM 服务远程调用系统服务进行用户认证。
 * 包含用户名和密码等认证信息。
 * </p>
 */
@Data
@Schema(description = "用户认证请求")
public class UserAuthenticationRequestDTO {

    /**
     * 用户名
     * <p>
     * 用于登录的唯一标识符，不允许为空。
     * </p>
     */
    @Schema(description = "用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     * <p>
     * 用户登录密码，不允许为空。
     * </p>
     */
    @Schema(description = "密码", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;
}
