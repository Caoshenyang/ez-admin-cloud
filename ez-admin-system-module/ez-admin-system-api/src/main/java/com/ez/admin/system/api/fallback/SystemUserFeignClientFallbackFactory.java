package com.ez.admin.system.api.fallback;

import com.ez.admin.feign.exception.BusinessException;
import com.ez.admin.system.api.dto.RolePermissionVO;
import com.ez.admin.system.api.dto.UserAuthenticationRequestDTO;
import com.ez.admin.system.api.dto.UserAuthenticationVO;
import com.ez.admin.system.api.dto.UserRoleVO;
import com.ez.admin.system.api.feign.SystemUserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 系统用户 Feign 客户端降级工厂
 * <p>
 * 当 Feign 调用失败时，提供降级处理逻辑。
 * 常见失败场景：
 * <ul>
 *   <li>网络异常</li>
 *   <li>服务不可用（服务宕机或超时）</li>
 *   <li>服务熔断（达到熔断阈值）</li>
 * </ul>
 * </p>
 * <p>
 * 注意：由于使用了 {@link com.ez.admin.feign.decoder.FeignResultDecoder} 自动解包 SaResult，
 * 降级处理需要直接返回业务对象或抛出异常。
 * </p>
 *
 * @see <a href="https://resilience4j.readme.io/docs/circuitbreaker">Resilience4j Circuit Breaker</a>
 */
@Slf4j
@Component
public class SystemUserFeignClientFallbackFactory implements FallbackFactory<SystemUserFeignClient> {

    /**
     * 创建降级处理器
     * <p>
     * 当 Feign 调用失败时，返回一个实现类的代理对象，执行降级逻辑。
     * </p>
     *
     * @param cause 失败原因
     * @return SystemUserFeignClient 代理对象
     */
    @Override
    public SystemUserFeignClient create(Throwable cause) {
        log.error("系统用户 Feign 调用失败，触发降级逻辑", cause);

        return new SystemUserFeignClient() {
            @Override
            public UserAuthenticationVO authenticateUser(UserAuthenticationRequestDTO requestDTO) {
                log.error("用户认证接口调用失败: username={}, error={}",
                        requestDTO.getUsername(), cause.getMessage());
                // 抛出业务异常，由全局异常处理器统一处理
                throw new BusinessException("系统服务暂时不可用，请稍后重试", 503);
            }

            @Override
            public List<RolePermissionVO> getAllRolePermissions() {
                log.error("获取所有角色权限接口调用失败: error={}", cause.getMessage());
                // 返回空列表，避免级联失败
                return Collections.emptyList();
            }

            @Override
            public UserRoleVO getUserRoles(Long userId) {
                log.error("获取用户角色接口调用失败: userId={}, error={}", userId, cause.getMessage());
                // 返回空对象，避免级联失败
                UserRoleVO emptyVO = new UserRoleVO();
                emptyVO.setUserId(userId);
                emptyVO.setRoleIds(Collections.emptyList());
                emptyVO.setRoleLabels(Collections.emptyList());
                return emptyVO;
            }
        };
    }
}
