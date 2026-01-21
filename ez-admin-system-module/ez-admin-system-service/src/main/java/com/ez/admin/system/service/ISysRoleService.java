package com.ez.admin.system.service;

import com.ez.admin.system.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ez.admin.system.model.vo.RolePermissionVO;

import java.util.List;

/**
 * <p>
 * 角色信息表 服务类
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 查询所有角色及其关联的权限标识列表
     * <p>
     * 供 IAM 服务在启动时调用，用于初始化角色-权限缓存。
     * </p>
     *
     * @return 角色权限VO列表
     */
    List<RolePermissionVO> getAllRolePermissions();
}
