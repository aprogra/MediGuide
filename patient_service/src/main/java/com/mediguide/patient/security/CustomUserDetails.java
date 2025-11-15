package com.mediguide.patient.security;

import com.mediguide.patient.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 自定义用户详情类
 * 扩展Spring Security的UserDetails，包含更多用户信息
 * 
 * @author MediGuide
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "password") // 为了安全，不打印密码
public class CustomUserDetails implements UserDetails {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 用户状态
     */
    private User.UserStatus status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 用户权限列表
     */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 账户是否过期
     */
    private boolean accountNonExpired = true;

    /**
     * 账户是否锁定
     */
    private boolean accountNonLocked = true;

    /**
     * 凭证是否过期
     */
    private boolean credentialsNonExpired = true;

    /**
     * 账户是否启用
     */
    private boolean enabled = true;

    /**
     * 全参构造函数
     * 
     * @param id 用户ID
     * @param username 用户名
     * @param password 密码
     * @param realName 真实姓名
     * @param email 邮箱
     * @param phone 手机号
     * @param role 角色
     * @param status 状态
     * @param lastLoginTime 最后登录时间
     * @param loginCount 登录次数
     * @param authorities 权限列表
     */
    public CustomUserDetails(Long id, String username, String password, 
                           String realName, String email, String phone, 
                           String role, User.UserStatus status, 
                           LocalDateTime lastLoginTime, Integer loginCount,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.lastLoginTime = lastLoginTime;
        this.loginCount = loginCount;
        this.authorities = authorities;
        
        // 根据用户状态设置账户属性
        updateAccountStatus();
    }

    /**
     * 根据用户状态更新账户属性
     */
    private void updateAccountStatus() {
        if (status == User.UserStatus.DISABLED) {
            this.enabled = false;
        } else if (status == User.UserStatus.LOCKED) {
            this.accountNonLocked = false;
        }
    }

    /**
     * 获取用户权限
     * 
     * @return 权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 获取密码
     * 
     * @return 密码
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 获取用户名
     * 
     * @return 用户名
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 账户是否未过期
     * 
     * @return true-未过期，false-已过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    /**
     * 账户是否未锁定
     * 
     * @return true-未锁定，false-已锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * 凭证是否未过期
     * 
     * @return true-未过期，false-已过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    /**
     * 账户是否启用
     * 
     * @return true-启用，false-禁用
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 检查用户是否具有指定角色
     * 
     * @param role 角色名称
     * @return true-具有角色，false-不具有角色
     */
    public boolean hasRole(String role) {
        return this.role != null && this.role.equals(role);
    }

    /**
     * 检查用户是否具有指定权限
     * 
     * @param authority 权限名称
     * @return true-具有权限，false-不具有权限
     */
    public boolean hasAuthority(String authority) {
        return authorities != null && authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals(authority));
    }

    /**
     * 检查用户是否具有任意一个指定角色
     * 
     * @param roles 角色列表
     * @return true-具有任意角色，false-不具有任何角色
     */
    public boolean hasAnyRole(String... roles) {
        if (this.role == null) {
            return false;
        }
        for (String role : roles) {
            if (this.role.equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查用户是否具有任意一个指定权限
     * 
     * @param authorities 权限列表
     * @return true-具有任意权限，false-不具有任何权限
     */
    public boolean hasAnyAuthority(String... authorities) {
        if (this.authorities == null) {
            return false;
        }
        for (String authority : authorities) {
            if (hasAuthority(authority)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取用户状态的中文描述
     * 
     * @return 状态描述
     */
    public String getStatusDescription() {
        if (status == null) {
            return "未知";
        }
        
        switch (status) {
            case ENABLED:
                return "正常";
            case DISABLED:
                return "禁用";
            case LOCKED:
                return "锁定";
            default:
                return "未知";
        }
    }

    /**
     * 检查用户是否为管理员
     * 
     * @return true-是管理员，false-不是管理员
     */
    public boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("SYSTEM_ADMIN");
    }

    /**
     * 检查用户是否为医生
     * 
     * @return true-是医生，false-不是医生
     */
    public boolean isDoctor() {
        return hasRole("DOCTOR");
    }

    /**
     * 检查用户是否为病人
     * 
     * @return true-是病人，false-不是病人
     */
    public boolean isPatient() {
        return hasRole("PATIENT");
    }

    /**
     * 检查用户是否为护士
     * 
     * @return true-是护士，false-不是护士
     */
    public boolean isNurse() {
        return hasRole("NURSE");
    }

    /**
     * 检查用户是否为药师
     * 
     * @return true-是药师，false-不是药师
     */
    public boolean isPharmacist() {
        return hasRole("PHARMACIST");
    }

    /**
     * 获取用户简要信息
     * 
     * @return 简要信息字符串
     */
    public String getBriefInfo() {
        return String.format("用户[id=%d, username=%s, role=%s, status=%s]", 
                id, username, role, getStatusDescription());
    }

    /**
     * 创建匿名用户详情
     * 
     * @return 匿名用户详情
     */
    public static CustomUserDetails createAnonymous() {
        return new CustomUserDetails(
                -1L,
                "anonymous",
                "",
                "匿名用户",
                "",
                "",
                "ANONYMOUS",
                User.UserStatus.ENABLED,
                null,
                0,
                new ArrayList<>()
        );
    }
}