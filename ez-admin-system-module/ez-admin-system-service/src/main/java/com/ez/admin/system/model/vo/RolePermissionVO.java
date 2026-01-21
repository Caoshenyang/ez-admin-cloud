package com.ez.admin.system.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 角色权限 VO
 * <p>
 * 用于接收角色及其关联的权限标识列表的查询结果。
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色标识
     */
    private String roleLabel;

    /**
     * 权限标识列表（菜单的 menu_perm 集合）
     */
    private List<String> permissions;
}
