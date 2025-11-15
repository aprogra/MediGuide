package com.mediguide.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类 (简化版本，使用Hutool实现)
 */
@Slf4j
public class JwtUtils {

    /**
     * JWT密钥
     */
    private static final String SECRET_KEY = "mediguide-secret-key-2024";

    /**
     * 访问令牌过期时间（毫秒）
     */
    public static final long ACCESS_TOKEN_EXPIRATION = 3600 * 1000; // 1小时

    /**
     * 刷新令牌过期时间（毫秒）
     */
    public static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 3600 * 1000; // 7天

    /**
     * 生成JWT令牌
     *
     * @param username 用户名
     * @param secret   密钥
     * @param expire   过期时间(毫秒)
     * @return JWT令牌
     */
    public static String createToken(String username, String secret, long expire) {
        try {
            // 创建头部
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            
            // 创建载荷
            Map<String, Object> payload = new HashMap<>();
            payload.put("username", username);
            payload.put("exp", System.currentTimeMillis() + expire);
            payload.put("iat", System.currentTimeMillis());
            
            // 编码头部和载荷
            String headerJson = JSONUtil.toJsonStr(header);
            String payloadJson = JSONUtil.toJsonStr(payload);
            
            String headerBase64 = Base64.getUrlEncoder().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadBase64 = Base64.getUrlEncoder().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            
            // 创建签名
            String signature = createSignature(headerBase64, payloadBase64, secret);
            
            // 组合JWT
            return headerBase64 + "." + payloadBase64 + "." + signature;
        } catch (Exception e) {
            log.error("生成JWT令牌失败", e);
            return null;
        }
    }

    /**
     * 验证JWT令牌
     *
     * @param token  JWT令牌
     * @param secret 密钥
     * @return 是否有效
     */
    public static boolean validateToken(String token, String secret) {
        try {
            if (StrUtil.isBlank(token)) {
                return false;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            
            String headerBase64 = parts[0];
            String payloadBase64 = parts[1];
            String signature = parts[2];
            
            // 验证签名
            String expectedSignature = createSignature(headerBase64, payloadBase64, secret);
            if (!signature.equals(expectedSignature)) {
                return false;
            }
            
            // 验证过期时间
            String payloadJson = new String(Base64.getUrlDecoder().decode(payloadBase64), StandardCharsets.UTF_8);
            JSONObject payload = JSONUtil.parseObj(payloadJson);
            Long exp = payload.getLong("exp");
            
            if (exp == null || exp < System.currentTimeMillis()) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从JWT令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        try {
            if (StrUtil.isBlank(token)) {
                return null;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            String payloadBase64 = parts[1];
            String payloadJson = new String(Base64.getUrlDecoder().decode(payloadBase64), StandardCharsets.UTF_8);
            JSONObject payload = JSONUtil.parseObj(payloadJson);
            
            return payload.getStr("username");
        } catch (Exception e) {
            log.error("从JWT令牌中获取用户名失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 检查JWT令牌是否过期
     *
     * @param token JWT令牌
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            if (StrUtil.isBlank(token)) {
                return true;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return true;
            }
            
            String payloadBase64 = parts[1];
            String payloadJson = new String(Base64.getUrlDecoder().decode(payloadBase64), StandardCharsets.UTF_8);
            JSONObject payload = JSONUtil.parseObj(payloadJson);
            
            Long exp = payload.getLong("exp");
            if (exp == null) {
                return true;
            }
            
            return exp < System.currentTimeMillis();
        } catch (Exception e) {
            log.error("检查JWT令牌过期时间失败: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 获取JWT令牌的过期时间
     *
     * @param token JWT令牌
     * @return 过期时间
     */
    public static Date getExpirationDateFromToken(String token) {
        try {
            if (StrUtil.isBlank(token)) {
                return null;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            String payloadBase64 = parts[1];
            String payloadJson = new String(Base64.getUrlDecoder().decode(payloadBase64), StandardCharsets.UTF_8);
            JSONObject payload = JSONUtil.parseObj(payloadJson);
            
            Long exp = payload.getLong("exp");
            if (exp == null) {
                return null;
            }
            
            return new Date(exp);
        } catch (Exception e) {
            log.error("获取JWT令牌过期时间失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 刷新JWT令牌
     *
     * @param token  旧的JWT令牌
     * @param secret 密钥
     * @param expire 新的过期时间(毫秒)
     * @return 新的JWT令牌
     */
    public static String refreshToken(String token, String secret, long expire) {
        try {
            if (StrUtil.isBlank(token)) {
                return null;
            }
            
            String username = getUsernameFromToken(token);
            if (username == null) {
                return null;
            }
            
            return createToken(username, secret, expire);
        } catch (Exception e) {
            log.error("刷新JWT令牌失败", e);
            return null;
        }
    }

    /**
     * 解析JWT令牌获取载荷
     *
     * @param token JWT令牌
     * @return 载荷Map
     */
    public static Map<String, Object> getPayloadFromToken(String token) {
        try {
            if (StrUtil.isBlank(token)) {
                return null;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            String payloadBase64 = parts[1];
            String payloadJson = new String(Base64.getUrlDecoder().decode(payloadBase64), StandardCharsets.UTF_8);
            JSONObject payload = JSONUtil.parseObj(payloadJson);
            
            return payload.toBean(Map.class);
        } catch (Exception e) {
            log.error("解析JWT令牌载荷失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成访问令牌
     *
     * @param username 用户名
     * @param userId   用户ID
     * @param role     角色
     * @return 访问令牌
     */
    public static String generateAccessToken(String username, Long userId, String role) {
        return createToken(username, SECRET_KEY, ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * 生成刷新令牌
     *
     * @param username 用户名
     * @return 刷新令牌
     */
    public static String generateRefreshToken(String username) {
        return createToken(username, SECRET_KEY, REFRESH_TOKEN_EXPIRATION);
    }

    /**
     * 创建签名
     *
     * @param headerBase64  头部Base64
     * @param payloadBase64 载荷Base64
     * @param secret        密钥
     * @return 签名
     */
    private static String createSignature(String headerBase64, String payloadBase64, String secret) {
        String data = headerBase64 + "." + payloadBase64;
        return cn.hutool.crypto.digest.DigestUtil.sha256Hex(secret + data);
    }
}