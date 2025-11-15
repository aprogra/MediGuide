package com.mediguide.patient.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库中的用户表
 * 
 * @author MediGuide
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {

    /**
     * 用户ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名 - 唯一
     */
    @TableField("username")
    private String username;

    /**
     * 密码（已加密）
     */
    @TableField("password")
    private String password;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 角色（ADMIN, DOCTOR, PATIENT, NURSE, PHARMACIST, SYSTEM_ADMIN, USER）
     */
    @TableField("role")
    private String role;

    /**
     * 权限列表（JSON格式存储）
     */
    @TableField("permissions")
    private String permissions;

    /**
     * 用户状态（0-禁用，1-启用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 登录失败次数
     */
    @TableField("login_fail_count")
    private Integer loginFailCount;

    /**
     * 登录次数
     */
    @TableField("login_count")
    private Integer loginCount;

    /**
     * 账户锁定时间
     */
    @TableField("lock_time")
    private LocalDateTime lockTime;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 性别（0-未知，1-男，2-女）
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDateTime birthday;

    /**
     * 部门/科室ID
     */
    @TableField("department_id")
    private Long departmentId;

    /**
     * 职位
     */
    @TableField("position")
    private String position;

    /**
     * 工号/学号
     */
    @TableField("employee_id")
    private String employeeId;

    /**
     * 身份证
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新人ID
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 逻辑删除标记（0-未删除，1-已删除）
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;

    /**
     * 获取用户状态枚举
     */
    public UserStatus getUserStatus() {
        return UserStatus.fromCode(this.status);
    }

    /**
     * 设置用户状态枚举
     */
    public void setUserStatus(UserStatus status) {
        this.status = status.getCode();
    }

    /**
     * 获取性别枚举
     */
    public Gender getGenderEnum() {
        return Gender.fromCode(this.gender);
    }

    /**
     * 设置性别枚举
     */
    public void setGenderEnum(Gender gender) {
        this.gender = gender.getCode();
    }

    /**
     * 检查用户是否被锁定
     */
    public boolean isLocked() {
        return this.lockTime != null && this.lockTime.isAfter(LocalDateTime.now());
    }

    /**
     * 检查用户是否启用
     */
    public boolean isEnabled() {
        return this.status != null && this.status == UserStatus.ENABLED.getCode();
    }

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        DISABLED(0, "禁用"),
        ENABLED(1, "启用"),
        LOCKED(2, "锁定");

        private final Integer code;
        private final String description;

        UserStatus(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static UserStatus fromCode(Integer code) {
            for (UserStatus status : UserStatus.values()) {
                if (status.getCode().equals(code)) {
                    return status;
                }
            }
            return DISABLED;
        }
    }

    /**
     * 性别枚举
     */
    public enum Gender {
        UNKNOWN(0, "未知"),
        MALE(1, "男"),
        FEMALE(2, "女");

        private final Integer code;
        private final String description;

        Gender(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Gender fromCode(Integer code) {
            for (Gender gender : Gender.values()) {
                if (gender.getCode().equals(code)) {
                    return gender;
                }
            }
            return UNKNOWN;
        }
    }
}