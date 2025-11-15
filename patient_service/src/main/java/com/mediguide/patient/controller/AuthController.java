package com.mediguide.patient.controller;

import com.mediguide.common.entity.Result;
import com.mediguide.patient.dto.LoginRequest;
import com.mediguide.patient.dto.LoginResponse;
import com.mediguide.patient.security.CustomUserDetails;
import com.mediguide.patient.service.AuthService;
import com.mediguide.common.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录、登出、令牌刷新等认证相关功能
 * 
 * @author MediGuide
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${jwt.secret:mediguide-secret-key-2024}")
    private String secret;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @param request HTTP请求
     * @return 登录结果
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户使用用户名和密码进行登录")
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        
        log.info("用户登录请求: username={}, loginType={}, clientType={}", 
                loginRequest.getUsername(), loginRequest.getLoginType(), loginRequest.getClientType());
        
        try {
            // 设置登录IP和用户代理
            loginRequest.setLoginIp(getClientIp(request));
            loginRequest.setUserAgent(request.getHeader("User-Agent"));
            
            // 调用认证服务进行登录
            LoginResponse loginResponse = authService.login(loginRequest);
            
            log.info("用户登录成功: username={}", loginRequest.getUsername());
            
            return Result.success("登录成功", loginResponse);
            
        } catch (Exception e) {
            log.error("用户登录失败: username={}, error={}", loginRequest.getUsername(), e.getMessage());
            return Result.fail("登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     * 
     * @param request HTTP请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出系统")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT', 'NURSE', 'PHARMACIST', 'SYSTEM_ADMIN', 'USER')")
    public Result<String> logout(HttpServletRequest request) {
        
        try {
            // 获取当前认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                String username = userDetails.getUsername();
                
                log.info("用户登出请求: username={}", username);
                
                // 调用认证服务进行登出
                authService.logout(username);
                
                log.info("用户登出成功: username={}", username);
                
                return Result.success("登出成功");
            }
            
            return Result.success("登出成功");
            
        } catch (Exception e) {
            log.error("用户登出失败: error={}", e.getMessage());
            return Result.fail("登出失败: " + e.getMessage());
        }
    }

    /**
     * 刷新访问令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public Result<LoginResponse> refreshToken(
            @Parameter(description = "刷新令牌", required = true)
            @RequestParam("refreshToken") String refreshToken) {
        
        log.info("刷新令牌请求");
        
        try {
            // 验证刷新令牌
            if (!jwtUtils.validateToken(refreshToken, secret)) {
                return Result.fail(401, "刷新令牌无效或已过期");
            }
            
            // 从刷新令牌中获取用户名
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            
            // 调用认证服务刷新令牌
            LoginResponse loginResponse = authService.refreshToken(refreshToken, username);
            
            log.info("刷新令牌成功: username={}", username);
            
            return Result.success("令牌刷新成功", loginResponse);
            
        } catch (Exception e) {
            log.error("刷新令牌失败: error={}", e.getMessage());
            return Result.fail(401, "刷新令牌失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     * 
     * @return 当前用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT', 'NURSE', 'PHARMACIST', 'SYSTEM_ADMIN', 'USER')")
    public Result<LoginResponse.UserInfo> getCurrentUserInfo() {
        
        try {
            // 获取当前认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                String username = userDetails.getUsername();
                
                log.info("获取用户信息: username={}", username);
                
                // 调用认证服务获取用户信息
                LoginResponse.UserInfo userInfo = authService.getCurrentUserInfo(username);
                
                return Result.success("获取用户信息成功", userInfo);
            }
            
            return Result.fail(401, "未获取到用户信息");
            
        } catch (Exception e) {
            log.error("获取用户信息失败: error={}", e.getMessage());
            return Result.fail("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     * 
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "用户修改登录密码")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT', 'NURSE', 'PHARMACIST', 'SYSTEM_ADMIN', 'USER')")
    public Result<String> changePassword(
            @Parameter(description = "旧密码", required = true)
            @RequestParam("oldPassword") String oldPassword,
            @Parameter(description = "新密码", required = true)
            @RequestParam("newPassword") String newPassword) {
        
        try {
            // 获取当前认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                String username = userDetails.getUsername();
                
                log.info("修改密码请求: username={}", username);
                
                // 调用认证服务修改密码
                authService.changePassword(username, oldPassword, newPassword);
                
                log.info("修改密码成功: username={}", username);
                
                return Result.success("密码修改成功");
            }
            
            return Result.fail(401, "未获取到用户信息");
            
        } catch (Exception e) {
            log.error("修改密码失败: error={}", e.getMessage());
            return Result.fail("密码修改失败: " + e.getMessage());
        }
    }

    /**
     * 验证令牌有效性
     * 
     * @param token 令牌
     * @return 验证结果
     */
    @GetMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证JWT令牌的有效性")
    public Result<Boolean> validateToken(
            @Parameter(description = "令牌", required = true)
            @RequestParam("token") String token) {
        
        try {
            boolean isValid = jwtUtils.validateToken(token, secret);
            
            if (isValid) {
                return Result.success("令牌有效", true);
            } else {
                return Result.success("令牌无效或已过期", false);
            }
            
        } catch (Exception e) {
            log.error("验证令牌失败: error={}", e.getMessage());
            return Result.success("令牌验证失败", false);
        }
    }

    /**
     * 获取客户端IP地址
     * 
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        
        return ip;
    }
}