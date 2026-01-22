package com.ez.admin.feign.decoder;

import com.ez.admin.core.entity.R;
import com.ez.admin.core.enums.BusinessErrorCode;
import com.ez.admin.feign.exception.BusinessException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Feign 结果解码器 (优化版)
 * <p>
 * 使用延迟加载的 Decoder 避免循环依赖问题。
 * </p>
 */
@Slf4j
public class FeignResultDecoder implements Decoder {

    private final Decoder delegate;

    public FeignResultDecoder(ObjectProvider<FeignHttpMessageConverters> feignHttpMessageConverters) {
        // 传入 Spring Cloud 2025 要求的 ObjectProvider
        this.delegate = new SpringDecoder(feignHttpMessageConverters);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        // 1. HTTP 状态码前置检查 (4xx, 5xx)
        if (response.status() >= 400) {
            log.error("远程服务响应异常: status={}, url={}", response.status(), response.request().url());
            throw new DecodeException(response.status(), "HTTP 响应错误", response.request());
        }

        // 2. 空响应体处理
        if (response.body() == null) {
            return null;
        }

        // 3. 针对 void/Void 返回类型的兼容
        if (type == void.class || type == Void.class) {
            // 虽然接口不要求返回值，但仍需解析外层 R 以校验业务 code
            R<?> result = (R<?>) delegate.decode(response, TypeUtils.parameterize(R.class, Void.class));
            checkResult(result, response);
            return null;
        }

        // 4. 执行业务解包
        Type wrappedType;
        // 判断：如果 type 已经是以 R 开头的泛型了（比如 R<User>），就别再嵌套包装成 R<R<User>> 了
        if (TypeUtils.isAssignable(type, R.class)) {
            wrappedType = type;
        } else {
            wrappedType = TypeUtils.parameterize(R.class, type);
        }
        try {
            R<?> result = (R<?>) delegate.decode(response, wrappedType);

            // 提取出的核心校验方法
            checkResult(result, response);

            return result.getData();
        } catch (BusinessException e) {
            throw e; // 业务异常直接向上抛出
        } catch (Exception e) {
            log.error("Feign 解包解析失败: url={}", response.request().url(), e);
            throw new DecodeException(response.status(), "解析异常: " + e.getMessage(), response.request());
        }
    }

    /**
     * 核心校验逻辑：检查解包结果及业务状态码
     */
    private void checkResult(R<?> result, Response response) {
        if (result == null) {
            log.error("Feign 返回结果解析后为空: url={}", response.request().url());
            throw new DecodeException(response.status(), "远程服务返回空结果", response.request());
        }

        // 业务状态码校验 (使用 Objects.equals 防止 NPE)
        if (!Objects.equals(BusinessErrorCode.SUCCESS.getCode(), result.getCode())) {
            String errorMsg = result.getMessage() != null ? result.getMessage() : "远程服务调用失败";

            log.warn("Feign 业务异常: url={}, code={}, msg={}",
                    response.request().url(), result.getCode(), errorMsg);

            // 抛出自定义业务异常
            throw new BusinessException(errorMsg, result.getCode());
        }
    }
}