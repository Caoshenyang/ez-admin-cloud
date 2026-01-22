package com.ez.admin.system.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 角色权限 VO
 * <p>
 * 用于返回角色及其关联的权限标识列表。
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RolePermissionVO", description = "角色权限")
public class RolePermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色标识")
    private String roleLabel;

    @Schema(description = "权限标识列表（菜单的 menu_perm 集合）")
    private List<String> permissions;
}
