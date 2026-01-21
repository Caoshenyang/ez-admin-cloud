package com.ez.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ez.admin.system.entity.SysUser;
import com.ez.admin.system.mapper.SysUserMapper;
import com.ez.admin.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    /**
     * 根据用户名查询用户信息
     * <p>
     * 查询条件：
     * <ul>
     *   <li>用户名匹配</li>
     *   <li>未删除（is_deleted = 0）</li>
     * </ul>
     * </p>
     *
     * @param username 用户名
     * @return 用户信息，如果不存在返回 null
     */
    @Override
    public SysUser getUserByUsername(String username) {
        log.debug("根据用户名查询用户信息: username={}", username);

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        // MyBatis-Plus 的 @TableLogic 注解会自动处理 is_deleted 条件

        return baseMapper.selectOne(queryWrapper);
    }
}
