package com.ez.admin.feign.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * 业务异常
 * <p>
 * 用于 Feign 远程调用时，当服务端返回非成功状态码时抛出。
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 构造业务异常
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 构造业务异常
     *
     * @param message 错误信息
     * @param code    错误码
     */
    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 构造业务异常
     *
     * @param message 错误信息
     * @param cause    原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    /**
     * 构造业务异常
     *
     * @param message 错误信息
     * @param code    错误码
     * @param cause    原因
     */
    public BusinessException(String message, Integer code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
