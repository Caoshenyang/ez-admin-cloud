package com.ez.admin.system.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户认证 VO
 * <p>
 * 系统服务返回的用户认证信息，包含用户基本信息。
 * IAM 服务使用此信息进行后续的 Token 生成和权限验证。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户认证")
public class UserAuthenticationVO {

    /**
     * 用户 ID
     * <p>
     * 系统中用户的唯一标识符。
     * </p>
     */
    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    /**
     * 用户名
     * <p>
     * 用户的登录账号。
     * </p>
     */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /**
     * 用户昵称
     * <p>
     * 用户的显示名称。
     * </p>
     */
    @Schema(description = "用户昵称", example = "管理员")
    private String nickname;

    /**
     * 用户状态
     * <p>
     * 用户状态：0-禁用，1-正常。
     * 只有状态为正常的用户才能成功登录。
     * </p>
     */
    @Schema(description = "用户状态【0 禁用 1 正常】", example = "1")
    private Integer status;

    /**
     * 密码（加密后的）
     * <p>
     * 用于密码校验，实际使用时应该使用加密算法（如 BCrypt）进行比对。
     * </p>
     */
    @Schema(description = "加密后的密码")
    private String password;
}
