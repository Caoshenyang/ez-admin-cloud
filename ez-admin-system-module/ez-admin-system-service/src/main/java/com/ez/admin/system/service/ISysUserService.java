package com.ez.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ez.admin.system.entity.SysUser;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户信息
     * <p>
     * 用于用户登录认证，根据用户名查询用户基本信息和加密后的密码。
     * </p>
     *
     * @param username 用户名
     * @return 用户信息，如果不存在返回 null
     */
    SysUser getUserByUsername(String username);
}
