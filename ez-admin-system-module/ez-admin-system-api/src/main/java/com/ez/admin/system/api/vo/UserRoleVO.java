package com.ez.admin.system.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户角色 VO
 * <p>
 * 用于返回用户及其关联的角色列表。
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserRoleVO", description = "用户角色")
public class UserRoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;

    @Schema(description = "角色标识列表")
    private List<String> roleLabels;
}
