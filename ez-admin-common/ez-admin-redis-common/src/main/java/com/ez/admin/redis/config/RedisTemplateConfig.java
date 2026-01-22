package com.ez.admin.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

/**
 * Redis 自动化配置 (基于 Spring Boot 4 & Jackson 3.x)
 */
@Configuration
public class RedisTemplateConfig {

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 1. 创建 Redis 专用的 ObjectMapper
        ObjectMapper redisMapper = createRedisObjectMapper(objectMapper);

        // 2. 初始化序列化器
        // 注意：Spring Data Redis 的 GenericJacksonJsonRedisSerializer 在适配 Jackson 3 时，
        // 构造函数依然接受 ObjectMapper。
        GenericJacksonJsonRedisSerializer jsonSerializer = new GenericJacksonJsonRedisSerializer(redisMapper);

        // 3. 设置序列化策略
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 构建 Redis 专用的 Jackson 实例
     * 在 Jackson 3.x 中，推荐从现有 Mapper 的配置出发重新 build
     */
    private ObjectMapper createRedisObjectMapper(ObjectMapper objectMapper) {
        // 1. 配置安全白名单校验器
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType("com.ez.admin.")
                .allowIfSubType("java.lang.")
                .allowIfSubType("java.util.")
                .allowIfSubType("java.time.")
                .build();

        // 2. Jackson 3.x 推荐写法：使用 rebuild()
        // 这会自动继承 globalMapper 的所有 Module（包括时间处理和 Long 转换）
        return objectMapper.rebuild()
                // 额外添加 Redis 专用的多态类型支持
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL)
                .build();
    }
}