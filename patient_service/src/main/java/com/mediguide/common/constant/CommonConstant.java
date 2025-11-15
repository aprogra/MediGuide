package com.mediguide.common.constant;

/**
 * 通用常量类
 */
public class CommonConstant {
    
    /**
     * JWT相关常量
     */
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String JWT_SECRET = "mediguide-jwt-secret-key-2024";
    public static final long JWT_EXPIRATION = 86400000; // 24小时
    public static final long JWT_REFRESH_EXPIRATION = 604800000; // 7天
    
    /**
     * Redis相关常量
     */
    public static final String REDIS_KEY_PREFIX = "mediguide:";
    public static final String REDIS_USER_TOKEN_KEY = REDIS_KEY_PREFIX + "user:token:";
    public static final String REDIS_USER_INFO_KEY = REDIS_KEY_PREFIX + "user:info:";
    public static final String REDIS_USER_PERMISSION_KEY = REDIS_KEY_PREFIX + "user:permission:";
    
    /**
     * 用户相关常量
     */
    public static final String DEFAULT_PASSWORD = "123456";
    public static final String DEFAULT_AVATAR = "https://mediguide.com/default-avatar.png";
    
    /**
     * 状态常量
     */
    public static final Integer STATUS_NORMAL = 1;
    public static final Integer STATUS_DISABLED = 0;
    public static final Integer STATUS_LOCKED = 2;
    public static final Integer STATUS_PENDING = 3;
    
    /**
     * 删除标记常量
     */
    public static final Integer NOT_DELETED = 0;
    public static final Integer IS_DELETED = 1;
    
    /**
     * 性别常量
     */
    public static final Integer GENDER_UNKNOWN = 0;
    public static final Integer GENDER_MALE = 1;
    public static final Integer GENDER_FEMALE = 2;
    
    /**
     * 婚姻状态常量
     */
    public static final Integer MARITAL_SINGLE = 0;
    public static final Integer MARITAL_MARRIED = 1;
    public static final Integer MARITAL_DIVORCED = 2;
    public static final Integer MARITAL_WIDOWED = 3;
    
    /**
     * 响应状态码
     */
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int FORBIDDEN_CODE = 403;
    public static final int NOT_FOUND_CODE = 404;
    public static final int BAD_REQUEST_CODE = 400;
    
    /**
     * 响应消息
     */
    public static final String SUCCESS_MSG = "操作成功";
    public static final String ERROR_MSG = "操作失败";
    public static final String UNAUTHORIZED_MSG = "未认证";
    public static final String FORBIDDEN_MSG = "无权限";
    public static final String NOT_FOUND_MSG = "资源不存在";
    public static final String BAD_REQUEST_MSG = "请求参数错误";
    
    private CommonConstant() {
        // 私有构造函数，防止实例化
    }
}