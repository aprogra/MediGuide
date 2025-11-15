package com.mediguide.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求数据传输对象
 * 
 * @author MediGuide
 * @version 1.0
 */
@Data
@Schema(description = "登录请求对象")
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "用户名只能包含字母、数字、下划线和中文")
    @Schema(description = "用户名", example = "admin", required = true)
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "密码", example = "123456", required = true)
    private String password;

    /**
     * 验证码（可选，用于防止暴力破解）
     */
    @Schema(description = "验证码", example = "1234")
    private String captcha;

    /**
     * 验证码唯一标识（可选，用于验证码验证）
     */
    @Schema(description = "验证码唯一标识", example = "uuid-123456")
    private String captchaId;

    /**
     * 记住我（可选，用于记住登录状态）
     */
    @Schema(description = "记住我", example = "true")
    private Boolean rememberMe = false;

    /**
     * 登录类型（可选，用于区分不同登录方式）
     */
    @Schema(description = "登录类型", example = "PASSWORD")
    private String loginType = LoginType.PASSWORD.getCode();

    /**
     * 客户端类型（可选，用于区分不同客户端）
     */
    @Schema(description = "客户端类型", example = "WEB")
    private String clientType = ClientType.WEB.getCode();

    /**
     * 登录IP（可选，用于记录登录IP）
     */
    @Schema(description = "登录IP", example = "192.168.1.1")
    private String loginIp;

    /**
     * 用户代理（可选，用于记录登录设备信息）
     */
    @Schema(description = "用户代理", example = "Mozilla/5.0...")
    private String userAgent;

    /**
     * 登录类型枚举
     */
    public enum LoginType {
        PASSWORD("PASSWORD", "密码登录"),
        SMS("SMS", "短信登录"),
        EMAIL("EMAIL", "邮箱登录"),
        WECHAT("WECHAT", "微信登录"),
        ALIPAY("ALIPAY", "支付宝登录");

        private final String code;
        private final String description;

        LoginType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static LoginType fromCode(String code) {
            for (LoginType type : LoginType.values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
            return PASSWORD;
        }
    }

    /**
     * 客户端类型枚举
     */
    public enum ClientType {
        WEB("WEB", "网页端"),
        MOBILE("MOBILE", "移动端"),
        APP("APP", "APP"),
        MINI_PROGRAM("MINI_PROGRAM", "小程序"),
        DESKTOP("DESKTOP", "桌面端");

        private final String code;
        private final String description;

        ClientType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static ClientType fromCode(String code) {
            for (ClientType type : ClientType.values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
            return WEB;
        }
    }

    /**
     * 验证登录请求参数
     * 
     * @return 验证结果
     */
    public boolean validate() {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        if (username.length() < 3 || username.length() > 20) {
            return false;
        }
        if (password.length() < 6 || password.length() > 20) {
            return false;
        }
        return true;
    }

    /**
     * 获取登录类型枚举
     * 
     * @return 登录类型枚举
     */
    public LoginType getLoginTypeEnum() {
        return LoginType.fromCode(this.loginType);
    }

    /**
     * 获取客户端类型枚举
     * 
     * @return 客户端类型枚举
     */
    public ClientType getClientTypeEnum() {
        return ClientType.fromCode(this.clientType);
    }

    /**
     * 清理敏感信息
     * 用于日志记录时清理敏感信息
     */
    public void clearSensitiveInfo() {
        this.password = null;
        this.captcha = null;
    }

    /**
     * 转换为安全的字符串表示
     * 
     * @return 安全的字符串表示
     */
    @Override
    public String toString() {
        return "LoginRequest{" +
                "username='" + username + '\'' +
                ", password='***'" +
                ", captcha='" + (captcha != null ? "***" : null) + '\'' +
                ", captchaId='" + captchaId + '\'' +
                ", rememberMe=" + rememberMe +
                ", loginType='" + loginType + '\'' +
                ", clientType='" + clientType + '\'' +
                ", loginIp='" + loginIp + '\'' +
                ", userAgent='" + (userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 50)) + "..." : null) + '\'' +
                '}';
    }
}