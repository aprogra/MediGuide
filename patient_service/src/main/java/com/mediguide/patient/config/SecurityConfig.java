package com.mediguide.patient.config;

import com.mediguide.patient.security.JwtAuthenticationEntryPoint;
import com.mediguide.patient.security.JwtAuthenticationFilter;
import com.mediguide.patient.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 安全配置类
 * 配置认证、授权、JWT过滤器等安全相关功能
 * 
 * @author MediGuide
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器
     * 使用BCrypt算法进行密码加密
     * 
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     * 用于处理认证请求
     * 
     * @param config AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception 异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 认证提供者
     * 配置用户详情服务和密码编码器
     * 
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * CORS配置
     * 配置跨域请求
     * 
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 安全配置过滤器链
     * 配置HTTP安全策略、URL权限、JWT过滤器等
     * 
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF（使用JWT不需要CSRF）
                .csrf(AbstractHttpConfigurer::disable)
                
                // 配置CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // 配置异常处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                
                // 配置会话管理（无状态）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 配置URL权限
                .authorizeHttpRequests(authz -> authz
                        // 放行静态资源
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        
                        // 放行Swagger相关资源
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        
                        // 放行健康检查接口
                        .requestMatchers("/actuator/health", "/health").permitAll()
                        
                        // 放行认证相关接口
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // 放行验证码接口
                        .requestMatchers("/api/captcha/**").permitAll()
                        
                        // 放行公共接口
                        .requestMatchers("/api/public/**").permitAll()
                        
                        // 管理员接口需要ADMIN角色
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        // 医生接口需要DOCTOR角色
                        .requestMatchers("/api/doctor/**").hasRole("DOCTOR")
                        
                        // 患者接口需要PATIENT角色
                        .requestMatchers("/api/patient/**").hasRole("PATIENT")
                        
                        // 护士接口需要NURSE角色
                        .requestMatchers("/api/nurse/**").hasRole("NURSE")
                        
                        // 药师接口需要PHARMACIST角色
                        .requestMatchers("/api/pharmacist/**").hasRole("PHARMACIST")
                        
                        // 系统管理员接口需要SYSTEM_ADMIN角色
                        .requestMatchers("/api/system/**").hasRole("SYSTEM_ADMIN")
                        
                        // 其他接口需要认证
                        .anyRequest().authenticated()
                )
                
                // 添加JWT过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 白名单URL数组
     * 这些URL不需要认证即可访问
     * 
     * @return 白名单URL数组
     */
    private static final String[] WHITE_LIST_URL = {
            // 认证相关
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/refresh",
            "/api/auth/register",
            "/api/auth/reset-password",
            
            // 验证码
            "/api/captcha/generate",
            "/api/captcha/verify",
            
            // 健康检查
            "/actuator/health",
            "/health",
            
            // Swagger
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            
            // 静态资源
            "/static/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/favicon.ico"
    };

    /**
     * 检查URL是否在白名单中
     * 
     * @param requestUri 请求URI
     * @return 是否在白名单中
     */
    public static boolean isWhiteListUrl(String requestUri) {
        if (requestUri == null || requestUri.trim().isEmpty()) {
            return false;
        }
        
        for (String whiteUrl : WHITE_LIST_URL) {
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
}