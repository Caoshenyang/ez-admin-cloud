package com.ez.admin.core.exception;

import com.ez.admin.core.enums.ErrorCode;
import lombok.Getter;

/**
 * 基础异常类
 * <p>
 * 所有自定义异常的基类，携带错误码和错误信息。
 * </p>
 * <p>
 * <b>设计原则：</b>
 * <ul>
 *   <li>所有业务异常都应继承此类</li>
 *   <li>异常必须携带 {@link ErrorCode} 错误码</li>
 *   <li>支持自定义错误提示信息（如需动态拼接参数）</li>
 *   <li>异常堆栈应在全局异常处理器中统一记录，避免业务代码重复记录</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-22
 */
@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final ErrorCode errorCode;

    /**
     * 错误提示信息
     */
    private final String message;

    /**
     * 构造函数 - 使用错误码枚举
     *
     * @param errorCode 错误码枚举
     */
    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    /**
     * 构造函数 - 使用错误码枚举 + 自定义消息
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误提示信息
     */
    public BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    /**
     * 构造函数 - 使用错误码枚举 + 自定义消息 + 原始异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误提示信息
     * @param cause     原始异常
     */
    public BaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
    }

    /**
     * 构造函数 - 使用错误码枚举 + 原始异常
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常
     */
    public BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public Integer getCode() {
        return errorCode.getCode();
    }

    /**
     * 判断是否需要告警
     *
     * @return true=需要告警，false=不需要告警
     */
    public boolean needAlert() {
        return errorCode.needAlert();
    }
}
