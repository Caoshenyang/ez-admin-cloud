package com.ez.admin.satoken.common.handler;

import cn.dev33.satoken.exception.*;
import com.ez.admin.result.entity.R;
import com.ez.admin.result.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>
 * 统一处理系统中的各类异常，返回标准的 {@link R} 格式。
 * </p>
 * <p>
 * 处理的异常类型：
 * <ul>
 *   <li>Sa-Token 相关异常（未登录、缺少权限、Token 过期等）</li>
 *   <li>系统通用异常（运行时异常）</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    // ========== Sa-Token 相关异常 ==========

    /**
     * 拦截：未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public R<Void> handleNotLoginException(NotLoginException e) {
        log.warn("未登录异常: {}, message={}", e.getClass().getSimpleName(), e.getMessage());
        return R.fail(ResultCode.UNAUTHORIZED, "用户未登录，请先登录");
    }

    /**
     * 拦截：缺少权限异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public R<Void> handleNotPermissionException(NotPermissionException e) {
        log.warn("缺少权限异常: permission={}, message={}", e.getPermission(), e.getMessage());
        return R.fail(ResultCode.PERMISSION_DENIED, "缺少权限：" + e.getPermission());
    }

    /**
     * 拦截：缺少角色异常
     */
    @ExceptionHandler(NotRoleException.class)
    public R<Void> handleNotRoleException(NotRoleException e) {
        log.warn("缺少角色异常: role={}, message={}", e.getRole(), e.getMessage());
        return R.fail(ResultCode.ROLE_DENIED, "缺少角色：" + e.getRole());
    }

    /**
     * 拦截：二级认证校验失败异常
     */
    @ExceptionHandler(NotSafeException.class)
    public R<Void> handleNotSafeException(NotSafeException e) {
        log.warn("二级认证校验失败异常: service={}, message={}", e.getService(), e.getMessage());
        return R.fail(ResultCode.FORBIDDEN, "二级认证校验失败：" + e.getService());
    }

    /**
     * 拦截：服务封禁异常
     */
    @ExceptionHandler(DisableServiceException.class)
    public R<Void> handleDisableServiceException(DisableServiceException e) {
        log.warn("服务封禁异常: service={}, level={}, disableTime={}",
                e.getService(), e.getLevel(), e.getDisableTime());
        return R.fail(ResultCode.FORBIDDEN,
                String.format("当前账号 %s 服务已被封禁 (level=%d)，%d 秒后解封",
                        e.getService(), e.getLevel(), e.getDisableTime()));
    }

    /**
     * 拦截：Http Basic 校验失败异常
     */
    @ExceptionHandler(NotHttpBasicAuthException.class)
    public R<Void> handleNotHttpBasicAuthException(NotHttpBasicAuthException e) {
        log.warn("Http Basic 校验失败: message={}", e.getMessage());
        return R.fail(ResultCode.UNAUTHORIZED, e.getMessage());
    }

    // ========== 系统通用异常 ==========

    /**
     * 拦截：其它所有未捕获异常
     * <p>
     * 注意：这是兜底的异常处理，建议在具体业务中细化异常处理。
     * </p>
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常: {}, message={}", e.getClass().getSimpleName(), e.getMessage(), e);
        return R.fail(ResultCode.INTERNAL_ERROR, "系统内部错误，请稍后重试");
    }
}
