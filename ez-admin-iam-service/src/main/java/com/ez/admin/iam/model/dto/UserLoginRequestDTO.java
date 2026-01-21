package com.ez.admin.iam.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求 DTO
 * <p>
 * 用于接收用户登录请求，包含用户名和密码等认证信息。
 * 该类使用 Jakarta Validation 进行参数校验，确保数据的合法性。
 * </p>
 */
@Data
@Schema(description = "用户登录请求")
public class UserLoginRequestDTO {

    /**
     * 用户名
     * <p>
     * 用于登录的唯一标识符，不允许为空。
     * 可以是用户名、邮箱或手机号，具体格式取决于业务规则。
     * </p>
     */
    @Schema(description = "用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     * <p>
     * 用户登录密码，不允许为空。
     * 建议前端对密码进行加密传输（如使用 HTTPS + RSA 加密）。
     * </p>
     */
    @Schema(description = "密码", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;
}
