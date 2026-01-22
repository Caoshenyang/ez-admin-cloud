package com.ez.admin.redis.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 全局配置类
 * <p>
 * 统一配置 ObjectMapper，应用于以下场景：
 * <ul>
 *   <li>Web 层：Controller 返回 JSON 给前端</li>
 *   <li>Redis 层：RedisTemplate 序列化存储</li>
 *   <li>Feign 层：远程调用数据传输</li>
 * </ul>
 * </p>
 * <p>
 * 核心功能：
 * <ol>
 *   <li>解决前端 Long 类型精度丢失问题（JavaScript 最大安全整数是 2^53-1）</li>
 *   <li>统一全局时间格式（yyyy-MM-dd HH:mm:ss）</li>
 *   <li>忽略未知属性，提高容错性</li>
 * </ol>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-22
 */
@Configuration
public class JacksonConfig {

    /**
     * 全局时间格式化常量
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 配置全局 ObjectMapper
     * <p>
     * 使用 {@link Primary} 注解，确保此 Bean 优先级最高，覆盖 Spring Boot 默认配置。
     * </p>
     * <p>
     * 使用 {@link ConditionalOnMissingBean} 注解，允许用户在特定场景下自定义 ObjectMapper。
     * </p>
     *
     * @return 全局 ObjectMapper 实例
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
        return JsonMapper.builder()
                // ========== 时间模块配置 ==========
                // 注册 Java Time 模块，支持 LocalDateTime、LocalDate 等类型
                // 1. 直接通过 SimpleModule 覆盖 LocalDateTime 的序列化逻辑
                .addModule(new SimpleModule()
                        .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter))
                        .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter))
                        // 修复 Long 精度丢失
                        .addSerializer(Long.class, ToStringSerializer.instance)
                        .addSerializer(Long.TYPE, ToStringSerializer.instance))
                // ========== 全局序列化配置 ==========
                // 禁止将日期序列化为时间戳（使用配置的格式化字符串）
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                // 遇到未知属性时不报错（提高容错性，避免字段新增导致反序列化失败）
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();
    }
}
