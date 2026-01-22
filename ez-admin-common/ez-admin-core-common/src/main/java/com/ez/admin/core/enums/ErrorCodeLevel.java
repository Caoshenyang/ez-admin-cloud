package com.ez.admin.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误级别枚举
 * <p>
 * 定义分布式系统中错误的级别分类，采用分段式错误码规范的第一位。
 * </p>
 * <p>
 * 错误码格式：{错误级别}{服务代码}{具体错误码}（共7位）
 * <ul>
 *   <li>错误级别：1位（1-用户端，2-服务端，3-第三方）</li>
 *   <li>服务代码：3位（如 001-IAM服务，002-System服务）</li>
 *   <li>具体错误码：3位（000-999）</li>
 * </ul>
 * 示例：1001001 = 用户端错误(1) + IAM服务(001) + 用户名已存在(001)
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-22
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeLevel {

    /**
     * 用户端错误
     * <p>
     * 表示由于客户端输入、权限或操作不当导致的错误。
     * 此类错误不需要告警，但需要向用户返回明确的提示信息。
     * </p>
     * <p>
     * 典型场景：
     * <ul>
     *   <li>参数校验失败</li>
     *   <li>权限不足</li>
     *   <li>数据不存在</li>
     *   <li>业务规则校验失败（如库存不足）</li>
     * </ul>
     * </p>
     */
    CLIENT(1, "用户端错误"),

    /**
     * 服务端错误
     * <p>
     * 表示由于服务内部逻辑、数据库操作、配置问题等导致的错误。
     * 此类错误需要告警，并记录详细日志用于排查。
     * </p>
     * <p>
     * 典型场景：
     * <ul>
     *   <li>空指针异常</li>
     *   <li>数据库连接失败</li>
     *   <li>业务逻辑异常</li>
     *   <li>系统配置错误</li>
     * </ul>
     * </p>
     */
    SERVER(2, "服务端错误"),

    /**
     * 第三方服务错误
     * <p>
     * 表示由于调用第三方服务（如支付、短信、OSS等）失败导致的错误。
     * 此类错误需要告警，并记录详细的调用上下文。
     * </p>
     * <p>
     * 典型场景：
     * <ul>
     *   <li>支付网关超时</li>
     *   <li>短信发送失败</li>
     *   <li>OSS上传失败</li>
     *   <li>外部API调用失败</li>
     * </ul>
     * </p>
     */
    THIRD_PARTY(3, "第三方服务错误");

    /**
     * 级别代码
     */
    private final Integer code;

    /**
     * 级别描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 级别代码
     * @return ErrorCodeLevel 枚举，未找到返回 null
     */
    public static ErrorCodeLevel valueOf(Integer code) {
        for (ErrorCodeLevel level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }
}
