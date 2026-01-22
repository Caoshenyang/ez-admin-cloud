package com.ez.admin.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务代码枚举
 * <p>
 * 定义分布式系统中各个微服务的唯一编号，采用分段式错误码规范的中间三位。
 * </p>
 * <p>
 * 错误码格式：{错误级别}{服务代码}{具体错误码}（共7位）
 * <ul>
 *   <li>错误级别：1位（1-用户端，2-服务端，3-第三方）</li>
 *   <li>服务代码：3位（如下方定义）</li>
 *   <li>具体错误码：3位（000-999）</li>
 * </ul>
 * </p>
 * <p>
 * <b>服务代码分配规则：</b>
 * <ul>
 *   <li>000：全局/通用（不特定于某个服务）</li>
 *   <li>001-099：核心基础服务</li>
 *   <li>100-199：IAM认证授权服务</li>
 *   <li>200-299：系统管理服务</li>
 *   <li>300-399：业务服务预留</li>
 *   <li>900-999：第三方服务</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-22
 */
@Getter
@AllArgsConstructor
public enum ServiceCode {

    /**
     * 全局/通用
     * <p>用于不特定于某个微服务的通用错误码</p>
     */
    GLOBAL(000, "全局通用"),

    // ========== 核心基础服务 (001-099) ==========

    /**
     * 核心公共模块
     * <p>提供统一返回对象、全局配置、通用枚举等核心能力</p>
     */
    CORE_COMMON(001, "核心公共模块"),

    /**
     * 网关服务
     * <p>统一入口、路由转发、认证鉴权</p>
     */
    GATEWAY(002, "网关服务"),

    /**
     * 注册中心
     * <p>服务注册与发现</p>
     */
    REGISTRY(003, "注册中心"),

    /**
     * 配置中心
     * <p>统一配置管理</p>
     */
    CONFIG(004, "配置中心"),

    // ========== IAM认证授权服务 (100-199) ==========

    /**
     * IAM服务
     * <p>认证授权、用户登录、权限管理</p>
     */
    IAM(100, "IAM认证授权服务"),

    // ========== 系统管理服务 (200-299) ==========

    /**
     * 系统管理服务
     * <p>用户、角色、部门、菜单等系统基础功能管理</p>
     */
    SYSTEM(200, "系统管理服务"),

    // ========== 业务服务预留 (300-899) ==========
    // 根据实际业务需求逐步添加

    // ========== 第三方服务 (900-999) ==========

    /**
     * 微信服务
     * <p>微信登录、微信支付等</p>
     */
    WECHAT(900, "微信服务"),

    /**
     * 支付宝服务
     * <p>支付宝登录、支付宝支付等</p>
     */
    ALIPAY(901, "支付宝服务"),

    /**
     * 短信服务
     * <p>验证码、通知短信</p>
     */
    SMS(902, "短信服务"),

    /**
     * OSS存储服务
     * <p>文件上传、下载</p>
     */
    OSS(903, "OSS存储服务");

    /**
     * 服务代码（3位数字）
     */
    private final Integer code;

    /**
     * 服务名称
     */
    private final String name;

    /**
     * 根据代码获取枚举
     *
     * @param code 服务代码
     * @return ServiceCode 枚举，未找到返回 null
     */
    public static ServiceCode valueOf(Integer code) {
        for (ServiceCode serviceCode : values()) {
            if (serviceCode.getCode().equals(code)) {
                return serviceCode;
            }
        }
        return null;
    }

    /**
     * 获取格式化后的服务代码（3位，不足补0）
     *
     * @return 格式化后的服务代码
     */
    public String getFormattedCode() {
        return String.format("%03d", code);
    }
}
