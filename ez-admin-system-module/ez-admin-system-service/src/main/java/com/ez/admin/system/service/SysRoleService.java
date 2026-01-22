package com.ez.admin.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ez.admin.system.api.vo.RolePermissionVO;
import com.ez.admin.system.entity.SysRole;
import com.ez.admin.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色信息表 服务类
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

    private final SysRoleMapper sysRoleMapper;

    /**
     * 查询所有角色及其关联的权限标识列表
     * <p>
     * 供 IAM 服务在启动时调用，用于初始化角色-权限缓存。
     * </p>
     *
     * @return 角色权限VO列表
     */
    public List<RolePermissionVO> getAllRolePermissions() {
        log.debug("查询所有角色及其权限标识列表");
        return sysRoleMapper.selectAllRolePermissions();
    }
}
