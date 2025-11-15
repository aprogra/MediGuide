package com.mediguide.patient.service;

import com.mediguide.patient.dto.LoginRequest;
import com.mediguide.patient.dto.LoginResponse;

/**
 * 认证服务接口
 * 定义用户认证相关的业务逻辑
 * 
 * @author MediGuide
 * @version 1.0
 */
public interface AuthService {

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 用户登出
     * 
     * @param username 用户名
     */
    void logout(String username);

    /**
     * 刷新访问令牌
     * 
     * @param refreshToken 刷新令牌
     * @param username 用户名
     * @return 新的登录响应
     */
    LoginResponse refreshToken(String refreshToken, String username);

    /**
     * 获取当前用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    LoginResponse.UserInfo getCurrentUserInfo(String username);

    /**
     * 修改密码
     * 
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(String username, String oldPassword, String newPassword);
}