package com.ez.admin.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ez.admin.system.entity.SysUserRoleRelation;
import com.ez.admin.system.mapper.SysUserRoleRelationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色关联表 服务类
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserRoleRelationService extends ServiceImpl<SysUserRoleRelationMapper, SysUserRoleRelation> {

    private final SysUserRoleRelationMapper sysUserRoleRelationMapper;

    /**
     * 根据用户ID查询角色ID列表
     * <p>
     * 供 IAM 服务在用户登录时调用，用于缓存用户-角色关系。
     * </p>
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    public List<Long> getRoleIdsByUserId(Long userId) {
        log.debug("查询用户角色列表: userId={}", userId);
        return sysUserRoleRelationMapper.selectRoleIdsByUserId(userId);
    }
}
