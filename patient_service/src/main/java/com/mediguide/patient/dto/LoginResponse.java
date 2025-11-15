package com.mediguide.patient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录响应数据传输对象
 * 
 * @author MediGuide
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "登录响应对象")
public class LoginResponse {

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    /**
     * 刷新令牌
     */
    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    /**
     * 令牌类型
     */
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    /**
     * 访问令牌过期时间（秒）
     */
    @Schema(description = "访问令牌过期时间（秒）", example = "3600")
    private Long accessTokenExpiresIn;

    /**
     * 刷新令牌过期时间（秒）
     */
    @Schema(description = "刷新令牌过期时间（秒）", example = "604800")
    private Long refreshTokenExpiresIn;

    /**
     * 角色权限映射
     */
    @Schema(description = "角色权限映射")
    private RolePermission rolePermission;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserInfo userInfo;

    /**
     * 权限信息
     */
    @Schema(description = "权限信息")
    private PermissionInfo permissionInfo;

    /**
     * 过期时间（秒）
     */
    @Schema(description = "过期时间（秒）", example = "3600")
    private Long expiresIn;

    /**
     * 用户信息内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户信息")
    public static class UserInfo {
        /**
         * 用户ID
         */
        @Schema(description = "用户ID", example = "1")
        private Long userId;

        /**
         * 用户名
         */
        @Schema(description = "用户名", example = "admin")
        private String username;

        /**
         * 真实姓名
         */
        @Schema(description = "真实姓名", example = "张三")
        private String realName;

        /**
         * 邮箱
         */
        @Schema(description = "邮箱", example = "zhangsan@example.com")
        private String email;

        /**
         * 手机号
         */
        @Schema(description = "手机号", example = "13800138000")
        private String phone;

        /**
         * 头像URL
         */
        @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
        private String avatar;

        /**
         * 性别（0-未知，1-男，2-女）
         */
        @Schema(description = "性别", example = "1")
        private Integer gender;

        /**
         * 生日
         */
        @Schema(description = "生日")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime birthday;

        /**
         * 部门/科室名称
         */
        @Schema(description = "部门/科室名称", example = "内科")
        private String departmentName;

        /**
         * 职位
         */
        @Schema(description = "职位", example = "主治医师")
        private String position;

        /**
         * 工号/学号
         */
        @Schema(description = "工号/学号", example = "1001")
        private String employeeId;

        /**
         * 用户状态（0-禁用，1-启用）
         */
        @Schema(description = "用户状态", example = "1")
        private Integer status;

        /**
         * 最后登录时间
         */
        @Schema(description = "最后登录时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastLoginTime;

        /**
         * 登录次数
         */
        @Schema(description = "登录次数", example = "10")
        private Integer loginCount;

        /**
         * 创建时间
         */
        @Schema(description = "创建时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        /**
         * 角色
         */
        @Schema(description = "角色", example = "ADMIN")
        private String role;
    }

    /**
     * 权限信息内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "权限信息")
    public static class PermissionInfo {
        /**
         * 角色
         */
        @Schema(description = "角色", example = "ADMIN")
        private String role;

        /**
         * 角色描述
         */
        @Schema(description = "角色描述", example = "管理员")
        private String roleDescription;

        /**
         * 权限列表
         */
        @Schema(description = "权限列表", example = "[\"user:view\", \"user:edit\"]")
        private List<String> permissions;

        /**
         * 角色权限映射
         */
        @Schema(description = "角色权限映射")
        private List<RolePermission> rolePermissions;
    }

    /**
     * 角色权限映射内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "角色权限映射")
    public static class RolePermission {
        /**
         * 角色代码
         */
        @Schema(description = "角色代码", example = "ADMIN")
        private String role;

        /**
         * 角色名称
         */
        @Schema(description = "角色名称", example = "管理员")
        private String roleName;

        /**
         * 权限列表
         */
        @Schema(description = "权限列表", example = "[\"user:view\", \"user:edit\"]")
        private List<String> permissions;
    }

    /**
     * 快速构建登录响应对象
     * 
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     * @param accessTokenExpiresIn 访问令牌过期时间
     * @param refreshTokenExpiresIn 刷新令牌过期时间
     * @param userInfo 用户信息
     * @param permissionInfo 权限信息
     * @return 登录响应对象
     */
    public static LoginResponse build(String accessToken, String refreshToken, 
                                    Long accessTokenExpiresIn, Long refreshTokenExpiresIn,
                                    UserInfo userInfo, PermissionInfo permissionInfo) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .refreshTokenExpiresIn(refreshTokenExpiresIn)
                .userInfo(userInfo)
                .permissionInfo(permissionInfo)
                .build();
    }

    /**
     * 构建简单的登录响应对象（仅包含令牌）
     * 
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     * @param accessTokenExpiresIn 访问令牌过期时间
     * @param refreshTokenExpiresIn 刷新令牌过期时间
     * @return 登录响应对象
     */
    public static LoginResponse buildSimple(String accessToken, String refreshToken,
                                          Long accessTokenExpiresIn, Long refreshTokenExpiresIn) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .refreshTokenExpiresIn(refreshTokenExpiresIn)
                .build();
    }

    /**
     * 清理敏感信息
     * 用于日志记录时清理敏感信息
     */
    public void clearSensitiveInfo() {
        this.accessToken = null;
        this.refreshToken = null;
        if (this.userInfo != null) {
            this.userInfo.email = null;
            this.userInfo.phone = null;
            this.userInfo.birthday = null;
        }
    }

    /**
     * 转换为安全的字符串表示
     * 
     * @return 安全的字符串表示
     */
    @Override
    public String toString() {
        return "LoginResponse{" +
                "accessToken='" + (accessToken != null ? "***" : null) + '\'' +
                ", refreshToken='" + (refreshToken != null ? "***" : null) + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", accessTokenExpiresIn=" + accessTokenExpiresIn +
                ", refreshTokenExpiresIn=" + refreshTokenExpiresIn +
                ", userInfo=" + userInfo +
                ", permissionInfo=" + permissionInfo +
                '}';
    }
}