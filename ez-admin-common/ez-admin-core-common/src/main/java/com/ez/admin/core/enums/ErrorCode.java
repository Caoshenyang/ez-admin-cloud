package com.ez.admin.core.enums;

/**
 * 错误码接口
 * <p>
 * 定义错误码的统一契约，所有具体的错误码枚举都应实现此接口。
 * </p>
 * <p>
 * <b>分段式错误码规范：</b>
 * <pre>
 * 格式：{错误级别}{服务代码}{具体错误码}（共7位）
 *
 * 示例：1001001
 *   - 1：用户端错误
 *   - 001：IAM服务
 *   - 001：用户名已存在
 * </pre>
 * </p>
 * <p>
 * <b>错误级别定义：</b>
 * <ul>
 *   <li>1：用户端错误（参数校验、权限不足、业务规则校验失败等）</li>
 *   <li>2：服务端错误（系统异常、数据库错误、业务逻辑异常等）</li>
 *   <li>3：第三方服务错误（支付、短信、OSS等第三方服务调用失败）</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-22
 */
public interface ErrorCode {

    /**
     * 获取错误码
     * <p>
     * 返回7位数字格式的错误码，格式：{错误级别}{服务代码}{具体错误码}
     * </p>
     *
     * @return 错误码（如 1001001）
     */
    Integer getCode();

    /**
     * 获取错误提示信息
     * <p>
     * 返回对用户友好的错误提示，建议直接展示给用户。
     * </p>
     *
     * @return 错误提示信息
     */
    String getMessage();

    /**
     * 获取错误级别
     * <p>
     * 根据错误码的首位数字推断错误级别。
     * </p>
     *
     * @return 错误级别枚举
     */
    default ErrorCodeLevel getLevel() {
        int levelCode = Integer.parseInt(String.valueOf(getCode()).charAt(0) + "");
        return ErrorCodeLevel.valueOf(levelCode);
    }

    /**
     * 判断是否为用户端错误
     *
     * @return true=用户端错误，false=其他错误
     */
    default boolean isClientError() {
        return ErrorCodeLevel.CLIENT == getLevel();
    }

    /**
     * 判断是否为服务端错误
     *
     * @return true=服务端错误，false=其他错误
     */
    default boolean isServerError() {
        return ErrorCodeLevel.SERVER == getLevel();
    }

    /**
     * 判断是否为第三方服务错误
     *
     * @return true=第三方服务错误，false=其他错误
     */
    default boolean isThirdPartyError() {
        return ErrorCodeLevel.THIRD_PARTY == getLevel();
    }

    /**
     * 判断是否需要告警
     * <p>
     * 服务端错误和第三方服务错误需要告警。
     * </p>
     *
     * @return true=需要告警，false=不需要告警
     */
    default boolean needAlert() {
        return isServerError() || isThirdPartyError();
    }
}
