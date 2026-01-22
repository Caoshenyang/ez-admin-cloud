package com.ez.admin.core.entity;

import com.ez.admin.core.enums.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * 统一返回结果对象
 * <p>
 * 封装所有 API 接口的返回结果，包含：
 * <ul>
 *   <li>success：操作是否成功（前端判断的捷径）</li>
 *   <li>code：业务状态码（区分成功/失败类型）</li>
 *   <li>message：提示信息</li>
 *   <li>data：返回数据</li>
 *   <li>timestamp：响应时间戳（用于监控）</li>
 *   <li>traceId：链路追踪ID（用于问题排查）</li>
 * </ul>
 * </p>
 * <p>
 * 使用静态工厂方法创建实例，保证返回对象的规范性。
 * </p>
 *
 * @param <T> 返回数据类型
 * @author ez-admin
 * @since 2026-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "R", description = "统一返回结果对象")
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== 核心字段 ==========

    /**
     * 操作是否成功
     * <p>
     * 前端可通过此字段快速判断操作是否成功。
     * 约定：code = 200 时 success = true，否则为 false。
     * </p>
     */
    @Schema(description = "操作是否成功")
    private Boolean success;

    /**
     * 业务状态码
     * <p>
     * 区分成功/失败类型，便于前端根据不同状态码进行差异化处理。
     * 详见 {@link ResultCode}。
     * </p>
     */
    @Schema(description = "业务状态码")
    private Integer code;

    /**
     * 提示信息
     * <p>
     * 对用户友好的提示文本，建议直接展示给用户。
     * </p>
     */
    @Schema(description = "提示信息")
    private String message;

    /**
     * 返回数据
     * <p>
     * 泛型数据，支持任意类型的业务数据。
     * </p>
     */
    @Schema(description = "返回数据")
    private T data;

    // ========== 运维与监控字段 ==========

    /**
     * 响应时间戳
     * <p>
     * 服务器处理请求的时间戳，可用于：
     * <ul>
     *   <li>前端计算请求耗时</li>
     *   <li>后端排查问题时定位时间点</li>
     * </ul>
     * </p>
     */
    @Schema(description = "响应时间戳")
    private Long timestamp;

    /**
     * 链路追踪ID
     * <p>
     * 用于分布式系统中追踪请求链路，问题排查神器。
     * 建议接入分布式追踪系统（如 Skywalking、Sleuth）后替换为真实 TraceId。
     * </p>
     */
    @Schema(description = "链路追踪ID")
    private String traceId;

    // ========== 私有构造方法 ==========

    /**
     * 私有构造，强制使用静态工厂方法创建实例
     * <p>
     * 通过 Builder 模式支持灵活构建。
     * </p>
     */
    private R(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = ResultCode.SUCCESS.getCode().equals(code);
        this.timestamp = System.currentTimeMillis();
        // TODO: 接入分布式追踪系统后，替换为真实 TraceId
        this.traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    // ========== 成功返回 ==========

    /**
     * 成功返回 - 带数据
     *
     * @param data 返回数据
     * @param <T>  数据类型
     * @return R 实例
     */
    public static <T> R<T> ok(T data) {
        return new R<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                data
        );
    }

    /**
     * 成功返回 - 不带数据
     *
     * @param <T> 数据类型
     * @return R 实例
     */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /**
     * 成功返回 - 自定义消息
     *
     * @param message 提示信息
     * @param data    返回数据
     * @param <T>     数据类型
     * @return R 实例
     */
    public static <T> R<T> ok(String message, T data) {
        return new R<>(
                ResultCode.SUCCESS.getCode(),
                message,
                data
        );
    }

    // ========== 失败返回 ==========

    /**
     * 失败返回 - 使用 ResultCode 枚举
     *
     * @param resultCode 业务状态码枚举
     * @param <T>        数据类型
     * @return R 实例
     */
    public static <T> R<T> fail(ResultCode resultCode) {
        return new R<>(
                resultCode.getCode(),
                resultCode.getMessage(),
                null
        );
    }

    /**
     * 失败返回 - 使用 ResultCode 枚举 + 自定义消息
     *
     * @param resultCode 业务状态码枚举
     * @param message    自定义提示信息
     * @param <T>        数据类型
     * @return R 实例
     */
    public static <T> R<T> fail(ResultCode resultCode, String message) {
        return new R<>(
                resultCode.getCode(),
                message,
                null
        );
    }

    /**
     * 失败返回 - 指定状态码和消息
     *
     * @param code    状态码
     * @param message 提示信息
     * @param <T>     数据类型
     * @return R 实例
     */
    public static <T> R<T> fail(Integer code, String message) {
        return new R<>(code, message, null);
    }

    /**
     * 失败返回 - 仅指定消息（使用默认错误码 5000）
     *
     * @param message 提示信息
     * @param <T>     数据类型
     * @return R 实例
     */
    public static <T> R<T> fail(String message) {
        return fail(ResultCode.INTERNAL_ERROR.getCode(), message);
    }

    /**
     * 失败返回 - 仅指定消息（使用系统繁忙错误码 5001）
     *
     * @param message 提示信息
     * @param <T>     数据类型
     * @return R 实例
     */
    public static <T> R<T> busy(String message) {
        return fail(ResultCode.SYSTEM_BUSY.getCode(), message);
    }

    // ========== 条件判断 ==========

    /**
     * 判断是否为成功状态
     *
     * @return true=成功，false=失败
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 判断是否为失败状态
     *
     * @return true=失败，false=成功
     */
    public boolean isFail() {
        return !isSuccess();
    }
}
