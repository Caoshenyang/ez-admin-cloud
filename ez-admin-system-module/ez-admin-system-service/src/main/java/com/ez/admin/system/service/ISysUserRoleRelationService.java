package com.ez.admin.system.service;

import com.ez.admin.system.entity.SysUserRoleRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户角色关联表 服务类
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
public interface ISysUserRoleRelationService extends IService<SysUserRoleRelation> {

    /**
     * 根据用户ID查询角色ID列表
     * <p>
     * 供 IAM 服务在用户登录时调用，用于缓存用户-角色关系。
     * </p>
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);
}
