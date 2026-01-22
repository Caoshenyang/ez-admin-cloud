package com.ez.admin.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务错误码枚举
 * <p>
 * 定义系统中所有业务相关的错误码，遵循分段式错误码规范。
 * </p>
 * <p>
 * <b>错误码格式：</b> {错误级别}{服务代码}{具体错误码}（共7位）
 * <ul>
 *   <li>错误级别：1-用户端，2-服务端，3-第三方</li>
 *   <li>服务代码：见 {@link ServiceCode}</li>
 *   <li>具体错误码：000-999</li>
 * </ul>
 * </p>
 * <p>
 * <b>使用示例：</b>
 * <pre>{@code
 * // 抛出业务异常
 * throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND);
 *
 * // 返回失败结果
 * return R.fail(BusinessErrorCode.USER_PASSWORD_ERROR);
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-22
 */
@Getter
@AllArgsConstructor
public enum BusinessErrorCode implements ErrorCode {

    // ==================== 全局通用错误 (错误级别1 + 服务代码000) ====================

    /**
     * 请求成功
     */
    SUCCESS(1000000, "操作成功"),

    /**
     * 请求参数错误
     */
    BAD_REQUEST(1000001, "请求参数错误"),

    /**
     * 参数校验失败
     */
    PARAM_VALIDATION_ERROR(1000002, "参数校验失败"),

    /**
     * 不支持的操作
     */
    OPERATION_NOT_SUPPORTED(1000003, "不支持的操作"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_SUPPORTED(1000004, "请求方法不支持"),

    /**
     * 请求媒体类型不支持
     */
    MEDIA_TYPE_NOT_SUPPORTED(1000005, "请求媒体类型不支持"),

    // ==================== 认证授权相关错误 (错误级别1 + 服务代码000) ====================

    /**
     * 未授权，请先登录
     */
    UNAUTHORIZED(1000100, "未授权，请先登录"),

    /**
     * Token 无效
     */
    TOKEN_INVALID(1000101, "Token 无效"),

    /**
     * Token 已过期
     */
    TOKEN_EXPIRED(1000102, "Token 已过期"),

    /**
     * Token 缺失
     */
    TOKEN_MISSING(1000103, "Token 缺失"),

    /**
     * 无权限访问
     */
    FORBIDDEN(1000104, "无权限访问"),

    /**
     * 权限不足
     */
    PERMISSION_DENIED(1000105, "权限不足"),

    // ==================== 全局服务端错误 (错误级别2 + 服务代码000) ====================

    /**
     * 系统内部错误
     */
    INTERNAL_ERROR(2000000, "系统内部错误"),

    /**
     * 系统繁忙，请稍后再试
     */
    SYSTEM_BUSY(2000001, "系统繁忙，请稍后再试"),

    /**
     * 服务暂时不可用
     */
    SERVICE_UNAVAILABLE(2000002, "服务暂时不可用"),

    /**
     * 数据库操作失败
     */
    DATABASE_ERROR(2000003, "数据库操作失败"),

    /**
     * 缓存操作失败
     */
    CACHE_ERROR(2000004, "缓存操作失败"),

    // ==================== 全局第三方服务错误 (错误级别3 + 服务代码000) ====================

    /**
     * 第三方服务调用失败
     */
    THIRD_PARTY_SERVICE_ERROR(3000000, "第三方服务调用失败"),

    /**
     * 第三方服务超时
     */
    THIRD_PARTY_SERVICE_TIMEOUT(3000001, "第三方服务超时"),

    // ==================== IAM 服务错误 (错误级别1/2 + 服务代码100) ====================

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1100001, "用户不存在"),

    /**
     * 用户名或密码错误
     */
    USER_PASSWORD_ERROR(1100002, "用户名或密码错误"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(1100003, "用户已被禁用"),

    /**
     * 用户名已存在
     */
    USERNAME_ALREADY_EXISTS(1100004, "用户名已存在"),

    /**
     * 验证码已过期
     */
    VERIFICATION_CODE_EXPIRED(1100005, "验证码已过期"),

    /**
     * 验证码错误
     */
    VERIFICATION_CODE_ERROR(1100006, "验证码错误"),

    /**
     * 验证码已发送，请勿重复获取
     */
    VERIFICATION_CODE_ALREADY_SENT(1100007, "验证码已发送，请勿重复获取"),

    /**
     * 原密码错误
     */
    OLD_PASSWORD_ERROR(1100008, "原密码错误"),

    /**
     * 新密码不能与原密码相同
     */
    SAME_PASSWORD_ERROR(1100009, "新密码不能与原密码相同"),

    // ==================== 系统管理服务错误 (错误级别1 + 服务代码200) ====================

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(1200001, "数据不存在"),

    /**
     * 数据已存在
     */
    DATA_ALREADY_EXISTS(1200002, "数据已存在"),

    /**
     * 部门下存在用户，无法删除
     */
    DEPT_HAS_USERS(1200101, "部门下存在用户，无法删除"),

    /**
     * 部门下存在子部门，无法删除
     */
    DEPT_HAS_CHILDREN(1200102, "部门下存在子部门，无法删除"),

    /**
     * 角色下存在用户，无法删除
     */
    ROLE_HAS_USERS(1200201, "角色下存在用户，无法删除"),

    /**
     * 菜单下存在子菜单，无法删除
     */
    MENU_HAS_CHILDREN(1200301, "菜单下存在子菜单，无法删除"),

    /**
     * 角色已分配菜单权限，无法删除
     */
    ROLE_HAS_MENUS(1200202, "角色已分配菜单权限，无法删除"),

    // ==================== 微信服务错误 (错误级别3 + 服务代码900) ====================

    /**
     * 微信登录失败
     */
    WECHAT_LOGIN_ERROR(3900001, "微信登录失败"),

    /**
     * 微信支付失败
     */
    WECHAT_PAY_ERROR(3900002, "微信支付失败"),

    // ==================== 短信服务错误 (错误级别3 + 服务代码902) ====================

    /**
     * 短信发送失败
     */
    SMS_SEND_ERROR(3902001, "短信发送失败"),

    /**
     * 短信发送频率过高
     */
    SMS_SEND_TOO_FREQUENT(3902002, "短信发送频率过高"),

    // ==================== OSS服务错误 (错误级别3 + 服务代码903) ====================

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(3903001, "文件上传失败"),

    /**
     * 文件下载失败
     */
    FILE_DOWNLOAD_ERROR(3903002, "文件下载失败"),

    /**
     * 文件大小超出限制
     */
    FILE_SIZE_EXCEEDED(3903003, "文件大小超出限制"),

    /**
     * 文件类型不支持
     */
    FILE_TYPE_NOT_SUPPORTED(3903004, "文件类型不支持"),
    ;

    /**
     * 错误码（7位数字）
     */
    private final Integer code;

    /**
     * 错误提示信息
     */
    private final String message;

    /**
     * 根据错误码获取枚举
     *
     * @param code 错误码
     * @return BusinessErrorCode 枚举，未找到返回 null
     */
    public static BusinessErrorCode valueOf(Integer code) {
        for (BusinessErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return null;
    }

    /**
     * 根据错误码获取错误提示信息
     *
     * @param code 错误码
     * @return 错误提示信息，未找到返回"未知错误"
     */
    public static String getMessageByCode(Integer code) {
        BusinessErrorCode errorCode = valueOf(code);
        return errorCode != null ? errorCode.getMessage() : "未知错误";
    }
}
