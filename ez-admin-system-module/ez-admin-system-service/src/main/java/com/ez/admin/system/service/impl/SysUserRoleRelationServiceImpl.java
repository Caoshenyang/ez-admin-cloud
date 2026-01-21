package com.ez.admin.system.service.impl;

import com.ez.admin.system.entity.SysUserRoleRelation;
import com.ez.admin.system.mapper.SysUserRoleRelationMapper;
import com.ez.admin.system.service.ISysUserRoleRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色关联表 服务实现类
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserRoleRelationServiceImpl extends ServiceImpl<SysUserRoleRelationMapper, SysUserRoleRelation> implements ISysUserRoleRelationService {

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
    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        log.debug("查询用户角色列表: userId={}", userId);
        return sysUserRoleRelationMapper.selectRoleIdsByUserId(userId);
    }
}
