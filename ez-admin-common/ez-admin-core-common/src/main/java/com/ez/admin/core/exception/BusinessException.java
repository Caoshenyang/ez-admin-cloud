package com.ez.admin.core.exception;

import com.ez.admin.core.enums.BusinessErrorCode;
import com.ez.admin.core.enums.ErrorCode;

/**
 * 业务异常类
 * <p>
 * 用于抛出业务相关的异常，通常由业务逻辑校验失败时抛出。
 * </p>
 * <p>
 * <b>使用场景：</b>
 * <ul>
 *   <li>数据不存在（如用户不存在）</li>
 *   <li>数据重复（如用户名已存在）</li>
 *   <li>业务规则校验失败（如库存不足）</li>
 *   <li>权限不足、状态不允许等业务限制</li>
 * </ul>
 * </p>
 * <p>
 * <b>使用示例：</b>
 * <pre>{@code
 * // 示例1：使用预定义错误码
 * if (user == null) {
 *     throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND);
 * }
 *
 * // 示例2：自定义错误提示信息
 * if (stock < quantity) {
 *     throw new BusinessException(BusinessErrorCode.STOCK_NOT_ENOUGH, "库存不足，当前库存：" + stock);
 * }
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-22
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数 - 使用业务错误码枚举
     *
     * @param errorCode 业务错误码枚举
     */
    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 构造函数 - 使用业务错误码枚举 + 自定义消息
     *
     * @param errorCode 业务错误码枚举
     * @param message   自定义错误提示信息
     */
    public BusinessException(BusinessErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 构造函数 - 使用业务错误码枚举 + 自定义消息 + 原始异常
     *
     * @param errorCode 业务错误码枚举
     * @param message   自定义错误提示信息
     * @param cause     原始异常
     */
    public BusinessException(BusinessErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 构造函数 - 使用业务错误码枚举 + 原始异常
     *
     * @param errorCode 业务错误码枚举
     * @param cause     原始异常
     */
    public BusinessException(BusinessErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    /**
     * 构造函数 - 使用通用错误码接口
     * <p>
     * 用于支持其他实现了 ErrorCode 接口的错误码枚举。
     * </p>
     *
     * @param errorCode 错误码接口
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 构造函数 - 使用通用错误码接口 + 自定义消息
     *
     * @param errorCode 错误码接口
     * @param message   自定义错误提示信息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 构造函数 - 使用通用错误码接口 + 自定义消息 + 原始异常
     *
     * @param errorCode 错误码接口
     * @param message   自定义错误提示信息
     * @param cause     原始异常
     */
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 构造函数 - 使用通用错误码接口 + 原始异常
     *
     * @param errorCode 错误码接口
     * @param cause     原始异常
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    // ========== 便捷静态工厂方法 ==========

    /**
     * 创建业务异常 - 使用错误码
     *
     * @param errorCode 错误码枚举
     * @return BusinessException 实例
     */
    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode);
    }

    /**
     * 创建业务异常 - 使用错误码 + 自定义消息
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误提示信息
     * @return BusinessException 实例
     */
    public static BusinessException of(ErrorCode errorCode, String message) {
        return new BusinessException(errorCode, message);
    }
}
