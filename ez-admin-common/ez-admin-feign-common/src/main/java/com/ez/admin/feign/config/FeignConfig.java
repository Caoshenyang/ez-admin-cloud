package com.ez.admin.feign.config;

import com.ez.admin.feign.decoder.FeignResultDecoder;
import feign.Request;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters;

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


    @Bean
    @Primary
    public Decoder feignDecoder(
            ObjectProvider<HttpMessageConverter<?>> messageConverters,
            ObjectProvider<HttpMessageConverterCustomizer> customizers) {

        // 1. 手动实例化那个你研究过的源码类
        FeignHttpMessageConverters feignConverters =
                new FeignHttpMessageConverters(messageConverters, customizers);

        // 2. 构造一个简单的 Provider 包装它
        ObjectProvider<FeignHttpMessageConverters> provider = new ObjectProvider<>() {
            @Override public FeignHttpMessageConverters getObject() { return feignConverters; }
            @Override public FeignHttpMessageConverters getObject(Object... args) { return feignConverters; }
            @Override public FeignHttpMessageConverters getIfAvailable() { return feignConverters; }
            @Override public FeignHttpMessageConverters getIfUnique() { return feignConverters; }
        };

        // 3. 注入到你的自定义解码器中
        return new FeignResultDecoder(provider);
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
