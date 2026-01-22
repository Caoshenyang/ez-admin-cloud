package com.ez.admin.system.controller;

import com.ez.admin.core.entity.R;
import com.ez.admin.system.api.dto.RolePermissionVO;
import com.ez.admin.system.entity.SysRole;
import com.ez.admin.system.service.ISysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色控制器
 * <p>
 * 提供角色相关的 REST API 接口，供 IAM 服务通过 Feign 远程调用。
 * 主要功能包括查询角色权限等。
 * 所有接口统一返回 {@link R} 格式。
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/system/role")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色相关接口（供内部服务调用）")
public class RoleController {

    private final ISysRoleService roleService;

    /**
     * 查询所有角色的权限列表
     * <p>
     * 此接口供 IAM 服务在启动时调用，用于初始化角色-权限缓存。
     * 实现步骤：
     * <ol>
     *   <li>查询所有角色及其关联的菜单权限</li>
     *   <li>提取每个角色的权限标识（menu_perm）列表</li>
     *   <li>返回角色权限列表</li>
     * </ol>
     * </p>
     *
     * @return 所有角色及其权限标识列表
     */
    @GetMapping("/permissions")
    @Operation(summary = "查询所有角色权限", description = "获取所有角色及其关联的权限标识列表")
    public R<List<RolePermissionVO>> getAllRolePermissions() {
        log.info("收到查询所有角色权限请求");

        List<com.ez.admin.system.model.vo.RolePermissionVO> rolePermissions = roleService.getAllRolePermissions();

        // 转换为API响应DTO
        List<RolePermissionVO> response = rolePermissions.stream()
                .map(vo -> {
                    RolePermissionVO dto = new RolePermissionVO();
                    dto.setRoleId(vo.getRoleId());
                    dto.setRoleLabel(vo.getRoleLabel());
                    dto.setPermissions(vo.getPermissions());
                    return dto;
                })
                .collect(Collectors.toList());

        log.info("查询所有角色权限成功: 角色数量={}", response.size());

        return R.ok(response);
    }
}
