package com.mediguide.patient.service;

import com.mediguide.common.entity.Result;
import com.mediguide.common.exception.BusinessException;
import com.mediguide.common.utils.EncryptUtils;
import com.mediguide.patient.dto.LoginRequest;
import com.mediguide.patient.dto.LoginResponse;
import com.mediguide.patient.entity.User;
import com.mediguide.patient.mapper.UserMapper;
import com.mediguide.patient.security.CustomUserDetails;
import com.mediguide.common.utils.JwtUtils;
import com.mediguide.common.constant.RoleConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户详情服务
 * 实现Spring Security的UserDetailsService接口，提供用户认证和授权功能
 * 
 * @author MediGuide
 * @version 1.0
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 根据用户名加载用户详情
     * 
     * @param username 用户名
     * @return 用户详情
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("加载用户详情: username={}", username);
        
        // 查询用户信息
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            log.error("用户不存在: username={}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        // 检查用户状态
        if (user.getStatus() != null && user.getStatus().equals(User.UserStatus.DISABLED.getCode())) {
            log.error("用户已被禁用: username={}", username);
            throw new BusinessException(403, "用户已被禁用");
        }
        
        if (user.getStatus() != null && user.getStatus().equals(User.UserStatus.LOCKED.getCode())) {
            log.error("用户已被锁定: username={}", username);
            throw new BusinessException(403, "用户已被锁定");
        }
        
        // 构建用户权限列表
        List<SimpleGrantedAuthority> authorities = buildUserAuthorities(user);
        
        // 创建自定义用户详情对象
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getUserStatus(),
                user.getLastLoginTime(),
                user.getLoginCount(),
                authorities
        );
        
        log.info("用户详情加载成功: username={}, role={}, authorities={}", 
                username, user.getRole(), authorities.size());
        
        return userDetails;
    }

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("用户登录服务: username={}", loginRequest.getUsername());
        
        try {
            // 1. 验证验证码（如果需要）
            if (loginRequest.getCaptcha() != null && !loginRequest.getCaptcha().isEmpty()) {
                validateCaptcha(loginRequest);
            }
            
            // 2. 执行Spring Security认证
            UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 3. 获取认证后的用户信息
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // 4. 生成JWT令牌
            String accessToken = jwtUtils.generateAccessToken(userDetails.getUsername(), userDetails.getId(), userDetails.getRole());
            String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());
            
            // 5. 更新用户登录信息
            updateUserLoginInfo(userDetails.getUsername(), loginRequest.getLoginIp());
            
            // 6. 构建登录响应
            LoginResponse loginResponse = buildLoginResponse(userDetails, accessToken, refreshToken);
            
            log.info("用户登录成功: username={}, userId={}", userDetails.getUsername(), userDetails.getId());
            
            return loginResponse;
            
        } catch (Exception e) {
            log.error("用户登录失败: username={}, error={}", loginRequest.getUsername(), e.getMessage());
            throw new BusinessException(401, "登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     * 
     * @param username 用户名
     */
    public void logout(String username) {
        log.info("用户登出服务: username={}", username);
        
        try {
            // 清除SecurityContext
            SecurityContextHolder.clearContext();
            
            // 可以在这里添加令牌黑名单等逻辑
            log.info("用户登出成功: username={}", username);
            
        } catch (Exception e) {
            log.error("用户登出失败: username={}, error={}", username, e.getMessage());
            throw new BusinessException("登出失败: " + e.getMessage());
        }
    }

    /**
     * 刷新访问令牌
     * 
     * @param refreshToken 刷新令牌
     * @param username 用户名
     * @return 新的登录响应
     */
    public LoginResponse refreshToken(String refreshToken, String username) {
        log.info("刷新令牌服务: username={}", username);
        
        try {
            // 1. 重新加载用户详情
            UserDetails userDetails = loadUserByUsername(username);
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            
            // 2. 生成新的访问令牌
            String newAccessToken = jwtUtils.generateAccessToken(
                    customUserDetails.getUsername(), 
                    customUserDetails.getId(), 
                    customUserDetails.getRole()
            );
            
            // 3. 构建登录响应
            LoginResponse loginResponse = buildLoginResponse(customUserDetails, newAccessToken, refreshToken);
            
            log.info("刷新令牌成功: username={}", username);
            
            return loginResponse;
            
        } catch (Exception e) {
            log.error("刷新令牌失败: username={}, error={}", username, e.getMessage());
            throw new BusinessException("刷新令牌失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    public LoginResponse.UserInfo getCurrentUserInfo(String username) {
        log.info("获取用户信息: username={}", username);
        
        try {
            // 重新加载用户详情
            UserDetails userDetails = loadUserByUsername(username);
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            
            // 构建用户信息
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setUserId(customUserDetails.getId());
            userInfo.setUsername(customUserDetails.getUsername());
            userInfo.setRealName(customUserDetails.getRealName());
            userInfo.setEmail(customUserDetails.getEmail());
            userInfo.setPhone(customUserDetails.getPhone());
            userInfo.setRole(customUserDetails.getRole());
            userInfo.setStatus(customUserDetails.getStatus().getCode());
            userInfo.setLastLoginTime(customUserDetails.getLastLoginTime());
            userInfo.setLoginCount(customUserDetails.getLoginCount());
            
            log.info("获取用户信息成功: username={}", username);
            
            return userInfo;
            
        } catch (Exception e) {
            log.error("获取用户信息失败: username={}, error={}", username, e.getMessage());
            throw new BusinessException("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     * 
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("修改密码服务: username={}", username);
        
        try {
            // 1. 验证旧密码
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            if (!EncryptUtils.matches(oldPassword, user.getPassword())) {
                throw new BusinessException("旧密码错误");
            }
            
            // 2. 加密新密码
            String encryptedNewPassword = EncryptUtils.encode(newPassword);
            
            // 3. 更新密码
            user.setPassword(encryptedNewPassword);
            user.setUpdateTime(LocalDateTime.now());
            
            int updatedRows = userMapper.updateById(user);
            if (updatedRows != 1) {
                throw new BusinessException("密码修改失败");
            }
            
            log.info("修改密码成功: username={}", username);
            
        } catch (BusinessException e) {
            log.error("修改密码失败: username={}, error={}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("修改密码失败: username={}, error={}", username, e.getMessage());
            throw new BusinessException("密码修改失败: " + e.getMessage());
        }
    }

    /**
     * 验证验证码
     * 
     * @param loginRequest 登录请求
     */
    private void validateCaptcha(LoginRequest loginRequest) {
        // 这里可以实现验证码验证逻辑
        // 例如从Redis中获取验证码进行验证
        log.info("验证码验证: captcha={}", loginRequest.getCaptcha());
        
        // 暂时跳过验证码验证
        // TODO: 实现验证码验证逻辑
    }

    /**
     * 构建用户权限列表
     * 
     * @param user 用户实体
     * @return 权限列表
     */
    private List<SimpleGrantedAuthority> buildUserAuthorities(User user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 添加角色权限
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        
        // 添加具体权限
        String role = user.getRole();
        String[] permissions = RoleConstant.getRolePermissions(role);
        
        if (permissions != null) {
            for (String permission : permissions) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }
        }
        
        return authorities;
    }

    /**
     * 更新用户登录信息
     * 
     * @param username 用户名
     * @param loginIp 登录IP
     */
    private void updateUserLoginInfo(String username, String loginIp) {
        try {
            User user = userMapper.selectByUsername(username);
            if (user != null) {
                user.setLastLoginIp(loginIp);
                user.setLastLoginTime(LocalDateTime.now());
                user.setLoginCount(user.getLoginCount() + 1);
                user.setUpdateTime(LocalDateTime.now());
                
                userMapper.updateById(user);
                
                log.info("更新用户登录信息成功: username={}, loginIp={}", username, loginIp);
            }
        } catch (Exception e) {
            log.error("更新用户登录信息失败: username={}, error={}", username, e.getMessage());
            // 不抛出异常，避免影响登录流程
        }
    }

    /**
     * 构建登录响应
     * 
     * @param userDetails 用户详情
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     * @return 登录响应
     */
    private LoginResponse buildLoginResponse(CustomUserDetails userDetails, String accessToken, String refreshToken) {
        LoginResponse loginResponse = new LoginResponse();
        
        // 设置令牌信息
        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setTokenType("Bearer");
        loginResponse.setExpiresIn(JwtUtils.ACCESS_TOKEN_EXPIRATION);
        
        // 设置用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(userDetails.getId());
        userInfo.setUsername(userDetails.getUsername());
        userInfo.setRealName(userDetails.getRealName());
        userInfo.setEmail(userDetails.getEmail());
        userInfo.setPhone(userDetails.getPhone());
        userInfo.setRole(userDetails.getRole());
        userInfo.setStatus(userDetails.getStatus().getCode());
        userInfo.setLastLoginTime(userDetails.getLastLoginTime());
        userInfo.setLoginCount(userDetails.getLoginCount());
        
        loginResponse.setUserInfo(userInfo);
        
        // 设置权限信息
        LoginResponse.PermissionInfo permissionInfo = new LoginResponse.PermissionInfo();
        permissionInfo.setRole(userDetails.getRole());
        permissionInfo.setPermissions(userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .filter(auth -> !auth.startsWith("ROLE_"))
                .collect(Collectors.toList()));
        
        loginResponse.setPermissionInfo(permissionInfo);
        
        // 设置角色权限映射
        LoginResponse.RolePermission rolePermission = new LoginResponse.RolePermission();
        rolePermission.setRole(userDetails.getRole());
        rolePermission.setRoleName(RoleConstant.getRoleName(userDetails.getRole()));
        rolePermission.setPermissions(Arrays.asList(RoleConstant.getRolePermissions(userDetails.getRole())));
        
        loginResponse.setRolePermission(rolePermission);
        
        return loginResponse;
    }
}