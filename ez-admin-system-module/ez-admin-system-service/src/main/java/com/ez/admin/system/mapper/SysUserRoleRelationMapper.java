package com.ez.admin.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ez.admin.system.entity.SysUserRoleRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户角色关联表 Mapper 接口
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
public interface SysUserRoleRelationMapper extends BaseMapper<SysUserRoleRelation> {

    /**
     * 根据用户ID查询角色ID列表
     * <p>
     * 查询指定用户关联的所有角色ID。
     * </p>
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}
