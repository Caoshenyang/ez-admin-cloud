package com.ez.admin.result.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务状态码枚举
 * <p>
 * 统一定义系统中的业务状态码，避免在代码中硬编码数字。
 * 状态码分段规则：
 * <ul>
 *   <li>200：操作成功</li>
 *   <li>400-499：客户端错误（参数校验、业务校验等）</li>
 *   <li>500-599：服务端错误（系统异常、第三方服务异常等）</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // ========== 成功 ==========
    SUCCESS(200, "操作成功"),

    // ========== 客户端错误 4xx ==========
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "请求资源不存在"),

    // 业务错误码：4001-4999
    USER_NOT_FOUND(4001, "用户不存在"),
    USER_DISABLED(4002, "用户已被禁用"),
    USER_PASSWORD_ERROR(4003, "用户名或密码错误"),
    PARAM_ERROR(4004, "参数校验失败"),
    DATA_NOT_FOUND(4005, "数据不存在"),
    DATA_ALREADY_EXISTS(4006, "数据已存在"),
    OPERATION_NOT_ALLOWED(4007, "不允许执行此操作"),

    // ========== 服务端错误 5xx ==========
    INTERNAL_ERROR(5000, "系统内部错误"),
    SYSTEM_BUSY(5001, "系统繁忙，请稍后再试"),
    SERVICE_UNAVAILABLE(5003, "服务暂时不可用"),
    REMOTE_CALL_FAILED(5004, "远程服务调用失败"),

    // ========== 认证授权相关 ==========
    TOKEN_INVALID(4010, "Token 无效"),
    TOKEN_EXPIRED(4011, "Token 已过期"),
    TOKEN_MISSING(4012, "Token 缺失"),
    PERMISSION_DENIED(4030, "权限不足"),
    ROLE_DENIED(4031, "角色权限不足"),

    // ========== 业务特定错误 ==========
    DEPT_HAS_USERS(4501, "部门下存在用户，无法删除"),
    DEPT_HAS_CHILDREN(4502, "部门下存在子部门，无法删除"),
    ROLE_HAS_USERS(4503, "角色下存在用户，无法删除"),
    MENU_HAS_CHILDREN(4504, "菜单下存在子菜单，无法删除"),
    ;

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 提示信息
     */
    private final String message;

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return ResultCode 枚举，未找到返回 null
     */
    public static ResultCode valueOf(Integer code) {
        for (ResultCode resultCode : values()) {
            if (resultCode.getCode().equals(code)) {
                return resultCode;
            }
        }
        return null;
    }

    /**
     * 判断是否为成功状态码
     *
     * @return true=成功，false=失败
     */
    public boolean isSuccess() {
        return SUCCESS.code.equals(this.code);
    }
}
