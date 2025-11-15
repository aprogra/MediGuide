package com.mediguide.common.utils;

import cn.hutool.crypto.digest.BCrypt;
import lombok.extern.slf4j.Slf4j;

/**
 * 加密工具类
 */
@Slf4j
public class EncryptUtils {

    /**
     * BCrypt密码加密
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        try {
            if (password == null) {
                return null;
            }
            return BCrypt.hashpw(password, BCrypt.gensalt());
        } catch (Exception e) {
            log.error("密码加密失败", e);
            return null;
        }
    }

    /**
     * 验证密码
     *
     * @param password       明文密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String password, String encryptedPassword) {
        try {
            if (password == null || encryptedPassword == null) {
                return false;
            }
            return BCrypt.checkpw(password, encryptedPassword);
        } catch (Exception e) {
            log.error("密码验证失败", e);
            return false;
        }
    }

    /**
     * 生成随机盐
     *
     * @return 随机盐
     */
    public static String generateSalt() {
        try {
            return BCrypt.gensalt();
        } catch (Exception e) {
            log.error("生成随机盐失败", e);
            return null;
        }
    }

    /**
     * 验证密码（兼容Spring Security的matches方法）
     *
     * @param password       明文密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String password, String encryptedPassword) {
        return verifyPassword(password, encryptedPassword);
    }

    /**
     * 加密密码（兼容Spring Security的encode方法）
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return encryptPassword(password);
    }
}