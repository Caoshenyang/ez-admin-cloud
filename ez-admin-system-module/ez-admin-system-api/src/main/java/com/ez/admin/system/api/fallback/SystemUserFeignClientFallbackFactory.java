package com.ez.admin.system.api.fallback;

import com.ez.admin.system.api.dto.UserAuthenticationRequestDTO;
import com.ez.admin.system.api.dto.UserAuthenticationResponseVO;
import com.ez.admin.system.api.feign.SystemUserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

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
            public UserAuthenticationResponseVO authenticateUser(UserAuthenticationRequestDTO requestDTO) {
                log.error("用户认证接口调用失败: username={}, error={}",
                        requestDTO.getUsername(), cause.getMessage());
                // 返回 null 或抛出自定义异常，根据业务需求决定
                throw new RuntimeException("系统服务暂时不可用，请稍后重试", cause);
            }
        };
    }
}
