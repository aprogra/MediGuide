package com.mediguide.patient.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediguide.common.entity.Result;
import com.mediguide.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器
 * 用于验证JWT令牌并设置用户认证信息
 * 
 * @author MediGuide
 * @version 1.0
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.secret:mediguide-secret-key-2024}")
    private String secret;

    /**
     * 白名单URL，不需要认证
     */
    private static final String[] WHITE_LIST_URLS = {
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/validate",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/webjars/**",
            "/actuator/**",
            "/error",
            "/favicon.ico"
    };

    /**
     * 过滤请求，验证JWT令牌
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        
        // 跳过白名单URL
        if (isWhiteListUrl(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // 从请求中获取JWT令牌
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt)) {
                // 验证JWT令牌
                if (jwtUtils.validateToken(jwt, secret)) {
                    // 从JWT中获取用户名
                    String username = jwtUtils.getUsernameFromToken(jwt);
                    
                    log.info("JWT认证成功: username={}, requestUri={}", 
                            username, requestUri);
                    
                    // 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 设置认证信息到SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                } else {
                    log.warn("JWT令牌无效: requestUri={}", requestUri);
                    handleAuthenticationError(response, "JWT令牌无效或已过期");
                    return;
                }
            } else {
                log.warn("未找到JWT令牌: requestUri={}", requestUri);
                handleAuthenticationError(response, "未提供JWT令牌");
                return;
            }
            
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期: requestUri={}, error={}", requestUri, e.getMessage());
            handleAuthenticationError(response, "JWT令牌已过期");
            return;
        } catch (JwtException e) {
            log.error("JWT令牌验证失败: requestUri={}, error={}", requestUri, e.getMessage());
            handleAuthenticationError(response, "JWT令牌验证失败");
            return;
        } catch (Exception e) {
            log.error("JWT认证处理失败: requestUri={}, error={}", requestUri, e.getMessage(), e);
            handleAuthenticationError(response, "认证处理失败");
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取JWT令牌
     * 
     * @param request HTTP请求
     * @return JWT令牌，如果未找到返回null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // 也可以从请求参数中获取
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }
        
        return null;
    }

    /**
     * 检查URL是否在白名单中
     * 
     * @param requestUri 请求URI
     * @return true-在白名单中，false-不在白名单中
     */
    private boolean isWhiteListUrl(String requestUri) {
        for (String whiteUrl : WHITE_LIST_URLS) {
            if (whiteUrl.endsWith("/**")) {
                String prefix = whiteUrl.substring(0, whiteUrl.length() - 3);
                if (requestUri.startsWith(prefix)) {
                    return true;
                }
            } else if (requestUri.equals(whiteUrl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理认证错误
     * 
     * @param response HTTP响应
     * @param message 错误消息
     * @throws IOException IO异常
     */
    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<Object> result = Result.fail(401, message);
        String jsonResponse = objectMapper.writeValueAsString(result);
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}