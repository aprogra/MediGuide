package com.mediguide.patient.service.impl;

import com.mediguide.patient.dto.LoginRequest;
import com.mediguide.patient.dto.LoginResponse;
import com.mediguide.patient.service.AuthService;
import com.mediguide.patient.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 * 实现用户认证相关的业务逻辑
 * 
 * @author MediGuide
 * @version 1.0
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("执行用户登录服务: username={}", loginRequest.getUsername());
        return userDetailsService.login(loginRequest);
    }

    /**
     * 用户登出
     * 
     * @param username 用户名
     */
    @Override
    public void logout(String username) {
        log.info("执行用户登出服务: username={}", username);
        userDetailsService.logout(username);
    }

    /**
     * 刷新访问令牌
     * 
     * @param refreshToken 刷新令牌
     * @param username 用户名
     * @return 新的登录响应
     */
    @Override
    public LoginResponse refreshToken(String refreshToken, String username) {
        log.info("执行刷新令牌服务: username={}", username);
        return userDetailsService.refreshToken(refreshToken, username);
    }

    /**
     * 获取当前用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public LoginResponse.UserInfo getCurrentUserInfo(String username) {
        log.info("执行获取用户信息服务: username={}", username);
        return userDetailsService.getCurrentUserInfo(username);
    }

    /**
     * 修改密码
     * 
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("执行修改密码服务: username={}", username);
        userDetailsService.changePassword(username, oldPassword, newPassword);
    }
}