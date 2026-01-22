package com.ez.admin.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ez.admin.system.api.vo.RolePermissionVO;
import com.ez.admin.system.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色信息表 Mapper 接口
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 查询所有角色及其关联的权限标识列表
     * <p>
     * 通过关联查询获取角色表和菜单表的数据，
     * 返回每个角色及其对应的权限标识（menu_perm）集合。
     * </p>
     *
     * @return 角色权限VO列表
     */
    List<RolePermissionVO> selectAllRolePermissions();
}
