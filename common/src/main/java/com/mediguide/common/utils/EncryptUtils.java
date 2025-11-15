package com.mediguide.common.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加密工具类
 * 提供密码加密、数据加密解密等安全相关功能
 * 
 * @author MediGuide
 * @version 1.0
 */
@Slf4j
@Component
public class EncryptUtils {

    /**
     * AES加密密钥 - 建议从配置文件中读取
     */
    @Value("${encrypt.aes-key:MediGuideAESKey2024ForDataEncryption}")
    private String aesKey;

    /**
     * BCrypt密码编码器实例
     */
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * 密码加密（BCrypt）
     * BCrypt算法会自动生成盐值，每次加密结果都不同
     * 
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return PASSWORD_ENCODER.encode(rawPassword);
    }

    /**
     * 密码验证
     * 
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        try {
            return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            log.error("密码验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * AES加密数据
     * 
     * @param data 原始数据
     * @param key 加密密钥
     * @return 加密后的Base64字符串
     */
    public String encryptData(String data, String key) {
        if (data == null || key == null) {
            throw new IllegalArgumentException("数据和密钥不能为空");
        }
        try {
            AES aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));
            byte[] encrypted = aes.encrypt(data);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("数据加密失败: {}", e.getMessage());
            throw new RuntimeException("数据加密失败", e);
        }
    }

    /**
     * AES解密数据
     * 
     * @param encryptedData 加密后的Base64字符串
     * @param key 解密密钥
     * @return 原始数据
     */
    public String decryptData(String encryptedData, String key) {
        if (encryptedData == null || key == null) {
            throw new IllegalArgumentException("加密数据和密钥不能为空");
        }
        try {
            AES aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = aes.decrypt(encryptedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("数据解密失败: {}", e.getMessage());
            throw new RuntimeException("数据解密失败", e);
        }
    }

    /**
     * 使用默认密钥加密数据
     * 
     * @param data 原始数据
     * @return 加密后的Base64字符串
     */
    public String encryptData(String data) {
        return encryptData(data, aesKey);
    }

    /**
     * 使用默认密钥解密数据
     * 
     * @param encryptedData 加密后的Base64字符串
     * @return 原始数据
     */
    public String decryptData(String encryptedData) {
        return decryptData(encryptedData, aesKey);
    }

    /**
     * 生成随机密钥
     * 
     * @param length 密钥长度（16、24、32）
     * @return 随机密钥
     */
    public static String generateRandomKey(int length) {
        if (length != 16 && length != 24 && length != 32) {
            throw new IllegalArgumentException("AES密钥长度必须是16、24或32字节");
        }
        return Base64.getEncoder().encodeToString(SecureUtil.generateKey("AES", length).getEncoded());
    }

    /**
     * 生成随机盐值
     * 
     * @param length 盐值长度
     * @return 随机盐值
     */
    public static String generateSalt(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("盐值长度必须大于0");
        }
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            salt.append(chars.charAt(index));
        }
        return salt.toString();
    }

    /**
     * Base64编码
     * 
     * @param data 原始数据
     * @return Base64编码字符串
     */
    public static String base64Encode(String data) {
        if (data == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解码
     * 
     * @param encodedData Base64编码字符串
     * @return 原始数据
     */
    public static String base64Decode(String encodedData) {
        if (encodedData == null) {
            return null;
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Base64解码失败: {}", e.getMessage());
            throw new RuntimeException("Base64解码失败", e);
        }
    }

    /**
     * 简单的数据脱敏处理
     * 用于敏感信息的显示，如手机号、身份证号等
     * 
     * @param data 原始数据
     * @param start 开始位置
     * @param end 结束位置
     * @return 脱敏后的数据
     */
    public static String maskData(String data, int start, int end) {
        if (data == null || data.length() <= start || data.length() <= end || start >= end) {
            return data;
        }
        StringBuilder masked = new StringBuilder(data);
        for (int i = start; i < end && i < data.length(); i++) {
            masked.setCharAt(i, '*');
        }
        return masked.toString();
    }

    /**
     * 手机号脱敏
     * 
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return maskData(phone, 3, 7);
    }

    /**
     * 身份证号脱敏
     * 
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 15) {
            return idCard;
        }
        return maskData(idCard, 4, idCard.length() - 4);
    }
}