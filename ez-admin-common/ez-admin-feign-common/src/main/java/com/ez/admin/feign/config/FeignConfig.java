package com.ez.admin.feign.config;

import com.ez.admin.feign.decoder.FeignResultDecoder;
import feign.Request;
import feign.codec.Decoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
     * 使用自定义的 FeignResultDecoder，自动解包 R&lt;T&gt;，
     * 让业务代码像调用本地方法一样调用远程接口。
     * </p>
     *
     * @param springDecoder Spring 默认的 Feign 解码器
     * @return 自定义解码器
     */
    @Bean
    @Primary
    public Decoder feignDecoder(Decoder springDecoder) {
        return new FeignResultDecoder(springDecoder);
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
                30, TimeUnit.SECONDS,
                // 允许重定向
                true
        );
    }
}
