package com.ez.admin.core.exception;

import com.ez.admin.core.entity.R;
import com.ez.admin.core.enums.BusinessErrorCode;
import com.ez.admin.core.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一拦截并处理系统中抛出的各类异常，将异常转换为标准的 {@link R} 响应。
 * </p>
 * <p>
 * <b>处理原则：</b>
 * <ul>
 *   <li>业务异常（BaseException）：返回对应的错误码和提示信息，记录 info 级别日志</li>
 *   <li>参数校验异常：返回参数错误信息，记录 debug 级别日志</li>
 *   <li>系统异常（Exception）：返回系统内部错误，记录 error 级别日志并告警</li>
 *   <li>所有响应均包含链路追踪ID，便于问题排查</li>
 * </ul>
 * </p>
 * <p>
 * <b>使用说明：</b>
 * <pre>
 * // 1. 在业务代码中抛出异常
 * throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND);
 *
 * // 2. 全局异常处理器会自动捕获并转换为 R 响应
 * // 响应：{"code": 1100001, "message": "用户不存在", "success": false, ...}
 * </pre>
 * </p>
 * <p>
 * <b>注意事项：</b>
 * <ul>
 *   <li>使用 {@code @ConditionalOnWebApplication} 确保仅在 Web 环境中生效</li>
 *   <li>日志中记录完整堆栈，便于问题排查</li>
 *   <li>生产环境注意敏感信息脱敏</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-22
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication
public class GlobalExceptionHandler {

    /**
     * 处理基础业务异常
     * <p>
     * 捕获所有继承自 {@link BaseException} 的业务异常。
     * 此类异常通常由业务逻辑校验失败时抛出，是业务流程的一部分。
     * </p>
     *
     * @param ex      业务异常
     * @param request HTTP 请求
     * @return 统一响应对象
     */
    @ExceptionHandler(BaseException.class)
    public R<Void> handleBaseException(BaseException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        Integer code = errorCode.getCode();
        String message = ex.getMessage();
        String uri = getRequestUri(request);

        // 根据错误级别决定日志级别和是否告警
        if (errorCode.needAlert()) {
            // 服务端错误和第三方服务错误需要告警
            log.error("[业务异常] URI: {}, 错误码: {}, 错误信息: {}", uri, code, message, ex);
        } else {
            // 用户端错误只记录 info 日志
            log.info("[业务异常] URI: {}, 错误码: {}, 错误信息: {}", uri, code, message);
        }

        return R.fail(code, message);
    }

    /**
     * 处理参数校验异常（@Valid 注解）
     * <p>
     * 捕获 {@link MethodArgumentNotValidException}，通常由 {@code @Valid} 或 {@code @Validated}
     * 注解触发，用于校验请求体中的参数。
     * </p>
     *
     * @param ex 参数校验异常
     * @return 统一响应对象
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.debug("[参数校验异常] 校验失败: {}", errorMessage);
        return R.fail(BusinessErrorCode.PARAM_VALIDATION_ERROR.getCode(), errorMessage);
    }

    /**
     * 处理参数绑定异常
     * <p>
     * 捕获 {@link BindException}，通常由表单参数绑定到对象时触发。
     * </p>
     *
     * @param ex 参数绑定异常
     * @return 统一响应对象
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.debug("[参数绑定异常] 绑定失败: {}", errorMessage);
        return R.fail(BusinessErrorCode.PARAM_VALIDATION_ERROR.getCode(), errorMessage);
    }

    /**
     * 处理约束违反异常（@Validated 注解）
     * <p>
     * 捕获 {@link ConstraintViolationException}，通常由 {@code @Validated} 注解
     * 在方法参数校验时触发。
     * </p>
     *
     * @param ex 约束违反异常
     * @return 统一响应对象
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.debug("[约束违反异常] 校验失败: {}", errorMessage);
        return R.fail(BusinessErrorCode.PARAM_VALIDATION_ERROR.getCode(), errorMessage);
    }

    /**
     * 处理请求方法不支持异常
     *
     * @param ex 请求方法不支持异常
     * @return 统一响应对象
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("请求方法 '%s' 不支持，支持的方法: %s",
                ex.getMethod(), String.join(", ", ex.getSupportedMethods()));

        log.debug("[请求方法不支持] {}", message);
        return R.fail(BusinessErrorCode.METHOD_NOT_SUPPORTED.getCode(), message);
    }

    /**
     * 处理参数类型不匹配异常
     *
     * @param ex 参数类型不匹配异常
     * @return 统一响应对象
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format("参数 '%s' 类型不匹配，期望类型: %s",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "未知");

        log.debug("[参数类型不匹配] {}", message);
        return R.fail(BusinessErrorCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理 404 异常
     *
     * @param ex      404 异常
     * @param request HTTP 请求
     * @return 统一响应对象
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Void> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        String uri = getRequestUri(request);
        String message = String.format("请求地址 '%s' 不存在", uri);

        log.debug("[404异常] {}", message);
        return R.fail(BusinessErrorCode.DATA_NOT_FOUND.getCode(), message);
    }

    /**
     * 处理所有未捕获的异常
     * <p>
     * 兜底处理，捕获所有未被上述方法处理的异常。
     * 此类异常通常是系统级别的严重错误，需要记录 error 日志并告警。
     * </p>
     *
     * @param ex      异常
     * @param request HTTP 请求
     * @return 统一响应对象
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception ex, HttpServletRequest request) {
        String uri = getRequestUri(request);
        String message = BusinessErrorCode.INTERNAL_ERROR.getMessage();

        // 记录完整的异常堆栈
        log.error("[系统异常] URI: {}, 异常类型: {}, 错误信息: {}",
                uri, ex.getClass().getSimpleName(), ex.getMessage(), ex);

        // 返回通用错误信息，避免暴露敏感的系统细节
        return R.fail(BusinessErrorCode.INTERNAL_ERROR.getCode(), message);
    }

    // ========== 私有辅助方法 ==========

    /**
     * 获取请求 URI
     *
     * @param request HTTP 请求
     * @return 请求 URI
     */
    private String getRequestUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
