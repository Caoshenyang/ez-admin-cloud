package com.ez.admin.feign.decoder;

import com.ez.admin.feign.exception.BusinessException;
import com.ez.admin.result.entity.R;
import com.ez.admin.result.enums.ResultCode;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Feign 结果解码器
 * <p>
 * 自动解包 R&lt;T&gt;，让业务代码像调用本地方法一样调用远程接口。
 * </p>
 * <p>
 * 核心流程：
 * <ol>
 *   <li>将服务端返回的 JSON 解码为 R&lt;T&gt;</li>
 *   <li>检查 code 是否为成功状态（200）</li>
 *   <li>如果成功，返回 data 中的业务对象</li>
 *   <li>如果失败，抛出 BusinessException</li>
 * </ol>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Slf4j
public class FeignResultDecoder implements Decoder {

    /**
     * 委托解码器（Spring 默认的解码器）
     */
    private final Decoder delegate;

    /**
     * 构造 Feign 结果解码器
     *
     * @param delegate 委托解码器
     */
    public FeignResultDecoder(Decoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        try {
            // 检查响应状态码
            if (response.status() >= 400) {
                throw new FeignException.BadRequest(
                        String.format("远程服务调用失败: status=%d, reason=%s",
                                response.status(), response.reason()),
                        response.request()
                );
            }

            // 检查响应体是否为空
            if (response.body() == null) {
                throw new FeignException.BadRequest(
                        "远程服务返回空结果",
                        response.request()
                );
            }

            // 构造 R<T> 的泛型类型
            Type wrappedType = ParametrizedTypeImpl.make(R.class, new Type[]{type}, null);

            // 使用委托解码器将响应解码为 R
            @SuppressWarnings("unchecked")
            R<?> result = (R<?>) delegate.decode(response, wrappedType);

            // 检查解码结果
            if (result == null) {
                log.error("Feign 解码结果为 null: url={}", response.request().url());
                throw new BusinessException("远程服务返回空结果");
            }

            // 检查业务状态码
            if (!ResultCode.SUCCESS.getCode().equals(result.getCode())) {
                String message = result.getMessage() != null ? result.getMessage() : "远程服务调用失败";
                log.error("Feign 远程调用失败: url={}, code={}, message={}",
                        response.request().url(), result.getCode(), message);
                throw new BusinessException(message, result.getCode());
            }

            // 返回 data 中的业务对象
            return result.getData();

        } catch (HttpMessageNotReadableException e) {
            log.error("Feign 响应解析失败: url={}, error={}", response.request().url(), e.getMessage());
            throw new FeignException.BadRequest(
                    String.format("响应解析失败: %s", e.getMessage()),
                    e,
                    response.request()
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Feign 解码异常: url={}, error={}", response.request().url(), e.getMessage(), e);
            throw new FeignException.BadRequest(
                    String.format("Feign 解码失败: %s", e.getMessage()),
                    e,
                    response.request()
            );
        }
    }
}
