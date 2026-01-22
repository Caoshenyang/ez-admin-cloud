package com.ez.admin.system.api.feign;

import com.ez.admin.system.api.dto.UserAuthenticationRequestDTO;
import com.ez.admin.system.api.vo.RolePermissionVO;
import com.ez.admin.system.api.vo.UserAuthenticationVO;
import com.ez.admin.system.api.vo.UserRoleVO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 系统用户 Feign 客户端接口
 * <p>
 * 供 IAM 服务远程调用系统服务的用户相关接口。
 * 使用 Spring Cloud OpenFeign 实现服务间通信。
 * </p>
 * <p>
 * 通过全局 Feign 配置的解码器，远程调用会自动解包 R&lt;T&gt;，
 * 业务代码无需关心 R 包装，直接使用返回的业务对象即可。
 * </p>
 *
 * @see <a href="https://spring.io/projects/spring-cloud-openfeign">Spring Cloud OpenFeign</a>
 */
@FeignClient(name = "ez-admin-system-service", path = "/api/v1/system")
public interface SystemUserFeignClient {


    @GetMapping("/role/permissions")
    @Operation(summary = "查询所有角色权限", description = "获取所有角色及其关联的权限标识列表")
    List<RolePermissionVO> getAllRolePermissions();


    /**
     * 根据用户名查询用户认证信息
     * <p>
     * 此接口供 IAM 服务在用户登录时调用，用于验证用户身份。
     * 实现步骤：
     * <ol>
     *   <li>IAM 服务接收用户登录请求</li>
     *   <li>IAM 服务调用此接口获取用户信息</li>
     *   <li>系统服务返回用户信息（包含加密后的密码）</li>
     *   <li>IAM 服务使用加密算法验证密码</li>
     *   <li>验证通过后，IAM 服务生成 Token 并返回</li>
     * </ol>
     * </p>
     * <p>
     * 注意：通过 Feign 自动解包，服务端返回的 SaResult&lt;UserAuthenticationVO&gt;
     * 会被自动解包为 UserAuthenticationVO。如果调用失败，会直接抛出 {@link com.ez.admin.feign.exception.BusinessException}。
     * </p>
     *
     * @param requestDTO 用户认证请求，包含用户名和密码
     * @return 用户认证信息，包含用户基本信息和加密密码
     * @throws com.ez.admin.feign.exception.BusinessException 远程调用失败时抛出
     */
    @PostMapping("/user/authenticate")
    @Operation(summary = "用户认证", description = "根据用户名查询用户认证信息")
    UserAuthenticationVO authenticateUser(@RequestBody UserAuthenticationRequestDTO requestDTO);



    /**
     * 根据用户ID查询用户角色信息
     * <p>
     * 此接口供 IAM 服务在用户登录时调用，用于缓存用户-角色关系。
     * </p>
     * <p>
     * 注意：通过 Feign 自动解包，服务端返回的 SaResult&lt;UserRoleVO&gt;
     * 会被自动解包为 UserRoleVO。
     * </p>
     *
     * @param userId 用户ID
     * @return 用户角色信息，包含用户关联的角色ID和角色标识列表
     * @throws com.ez.admin.feign.exception.BusinessException 远程调用失败时抛出
     */
    @GetMapping("/user/roles")
    @Operation(summary = "查询用户角色", description = "根据用户ID查询用户关联的角色列表")
    UserRoleVO getUserRoles(@RequestParam("userId") Long userId);
}
