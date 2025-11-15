package com.mediguide.common.constant;

/**
 * 角色常量类
 */
public class RoleConstant {
    
    /**
     * 系统管理员
     */
    public static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";
    
    /**
     * 管理员
     */
    public static final String ADMIN = "ADMIN";
    
    /**
     * 医生
     */
    public static final String DOCTOR = "DOCTOR";
    
    /**
     * 护士
     */
    public static final String NURSE = "NURSE";
    
    /**
     * 药师
     */
    public static final String PHARMACIST = "PHARMACIST";
    
    /**
     * 患者
     */
    public static final String PATIENT = "PATIENT";
    
    /**
     * 普通用户
     */
    public static final String USER = "USER";
    
    /**
     * 所有角色数组
     */
    public static final String[] ALL_ROLES = {
        SYSTEM_ADMIN, ADMIN, DOCTOR, NURSE, PHARMACIST, PATIENT, USER
    };

    /**
     * 获取角色名称
     * @param role 角色代码
     * @return 角色名称
     */
    public static String getRoleName(String role) {
        switch (role) {
            case SYSTEM_ADMIN:
                return "系统管理员";
            case ADMIN:
                return "管理员";
            case DOCTOR:
                return "医生";
            case NURSE:
                return "护士";
            case PHARMACIST:
                return "药师";
            case PATIENT:
                return "患者";
            case USER:
                return "普通用户";
            default:
                return "未知角色";
        }
    }

    /**
     * 获取角色权限
     * @param role 角色代码
     * @return 权限列表
     */
    public static String[] getRolePermissions(String role) {
        switch (role) {
            case SYSTEM_ADMIN:
                return new String[]{"system:*", "admin:*", "doctor:*", "patient:*", "nurse:*", "pharmacist:*"};
            case ADMIN:
                return new String[]{"admin:*", "doctor:*", "patient:*", "nurse:*", "pharmacist:*"};
            case DOCTOR:
                return new String[]{"doctor:*", "patient:view", "patient:update"};
            case NURSE:
                return new String[]{"nurse:*", "patient:view", "patient:update"};
            case PHARMACIST:
                return new String[]{"pharmacist:*", "patient:view"};
            case PATIENT:
                return new String[]{"patient:self"};
            case USER:
                return new String[]{"user:*"};
            default:
                return new String[]{};
        }
    }
    
    private RoleConstant() {
        // 私有构造函数，防止实例化
    }
}