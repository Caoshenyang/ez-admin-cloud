package com.ez.admin.feign.config;

import com.ez.admin.feign.decoder.FeignResultDecoder;
import feign.Request;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Feign 配置类
 * <p>
 * 提供 Feign 远程调用的公共配置，包括：
 * <ul>
 *   <li>自动解包解码器：将 SaResult&lt;T&gt; 自动解包为 T</li>
 *   <li>超时时间配置</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Configuration
public class FeignConfig {

    /**
     * 配置 Feign 解码器
     * <p>
     * 使用自定义的 FeignResultDecoder，自动解包 SaResult，
     * 让业务代码像调用本地方法一样调用远程接口。
     * </p>
     *
     * @param messageConverters HTTP 消息转换器工厂
     * @return 自定义解码器
     */
    @Bean
    public feign.codec.Decoder feignDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new FeignResultDecoder(
                new ResponseEntityDecoder(
                        new org.springframework.cloud.openfeign.support.SpringDecoder(messageConverters)
                )
        );
    }

    /**
     * 配置 Feign 请求选项
     * <p>
     * 设置连接超时和读取超时时间。
     * </p>
     *
     * @return 请求选项
     */
    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(
                // 连接超时时间：5 秒
                5, TimeUnit.SECONDS,
                // 读取超时时间：30 秒
                30, TimeUnit.SECONDS
        );
    }
}
