package com.ez.admin.system.api.feign;

import com.ez.admin.system.api.dto.UserAuthenticationRequestDTO;
import com.ez.admin.system.api.dto.UserAuthenticationResponseVO;
import com.ez.admin.system.api.fallback.SystemUserFeignClientFallbackFactory;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 系统用户 Feign 客户端接口
 * <p>
 * 供 IAM 服务远程调用系统服务的用户相关接口。
 * 使用 Spring Cloud OpenFeign 实现服务间通信。
 * </p>
 *
 * @see <a href="https://spring.io/projects/spring-cloud-openfeign">Spring Cloud OpenFeign</a>
 */
@FeignClient(
        name = "ez-admin-system-service",
        path = "/api/v1/system/user",
        fallbackFactory = SystemUserFeignClientFallbackFactory.class
)
public interface SystemUserFeignClient {

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
     *
     * @param requestDTO 用户认证请求，包含用户名和密码
     * @return 用户认证响应，包含用户基本信息和加密密码
     */
    @PostMapping("/authenticate")
    @Operation(summary = "用户认证", description = "根据用户名查询用户认证信息")
    UserAuthenticationResponseVO authenticateUser(@RequestBody UserAuthenticationRequestDTO requestDTO);
}
