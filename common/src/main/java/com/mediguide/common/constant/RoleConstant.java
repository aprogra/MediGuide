package com.mediguide.common.constant;

/**
 * 角色权限常量类
 * 定义系统中所有角色和权限相关的常量
 * 
 * @author MediGuide
 * @version 1.0
 */
public class RoleConstant {

    /**
     * ==================== 角色定义 ====================
     */
    
    /**
     * 管理员角色
     */
    public static final String ROLE_ADMIN = "ADMIN";
    
    /**
     * 医生角色
     */
    public static final String ROLE_DOCTOR = "DOCTOR";
    
    /**
     * 患者角色
     */
    public static final String ROLE_PATIENT = "PATIENT";
    
    /**
     * 护士角色
     */
    public static final String ROLE_NURSE = "NURSE";
    
    /**
     * 药师角色
     */
    public static final String ROLE_PHARMACIST = "PHARMACIST";
    
    /**
     * 系统管理员角色
     */
    public static final String ROLE_SYSTEM_ADMIN = "SYSTEM_ADMIN";
    
    /**
     * 普通用户角色（默认角色）
     */
    public static final String ROLE_USER = "USER";
    
    /**
     * ==================== 权限定义 ====================
     */
    
    /**
     * 用户管理权限
     */
    public static final String PERMISSION_USER_MANAGE = "user:manage";
    
    /**
     * 用户查看权限
     */
    public static final String PERMISSION_USER_VIEW = "user:view";
    
    /**
     * 用户编辑权限
     */
    public static final String PERMISSION_USER_EDIT = "user:edit";
    
    /**
     * 用户删除权限
     */
    public static final String PERMISSION_USER_DELETE = "user:delete";
    
    /**
     * 患者管理权限
     */
    public static final String PERMISSION_PATIENT_MANAGE = "patient:manage";
    
    /**
     * 患者查看权限
     */
    public static final String PERMISSION_PATIENT_VIEW = "patient:view";
    
    /**
     * 患者编辑权限
     */
    public static final String PERMISSION_PATIENT_EDIT = "patient:edit";
    
    /**
     * 医生管理权限
     */
    public static final String PERMISSION_DOCTOR_MANAGE = "doctor:manage";
    
    /**
     * 医生查看权限
     */
    public static final String PERMISSION_DOCTOR_VIEW = "doctor:view";
    
    /**
     * 医生编辑权限
     */
    public static final String PERMISSION_DOCTOR_EDIT = "doctor:edit";
    
    /**
     * 诊疗记录管理权限
     */
    public static final String PERMISSION_MEDICAL_RECORD_MANAGE = "medical:record:manage";
    
    /**
     * 诊疗记录查看权限
     */
    public static final String PERMISSION_MEDICAL_RECORD_VIEW = "medical:record:view";
    
    /**
     * 诊疗记录编辑权限
     */
    public static final String PERMISSION_MEDICAL_RECORD_EDIT = "medical:record:edit";
    
    /**
     * 药方管理权限
     */
    public static final String PERMISSION_PRESCRIPTION_MANAGE = "prescription:manage";
    
    /**
     * 药方查看权限
     */
    public static final String PERMISSION_PRESCRIPTION_VIEW = "prescription:view";
    
    /**
     * 药方编辑权限
     */
    public static final String PERMISSION_PRESCRIPTION_EDIT = "prescription:edit";
    
    /**
     * 系统设置权限
     */
    public static final String PERMISSION_SYSTEM_SETTING = "system:setting";
    
    /**
     * 系统日志查看权限
     */
    public static final String PERMISSION_SYSTEM_LOG_VIEW = "system:log:view";
    
    /**
     * 报表查看权限
     */
    public static final String PERMISSION_REPORT_VIEW = "report:view";
    
    /**
     * 报表导出权限
     */
    public static final String PERMISSION_REPORT_EXPORT = "report:export";
    
    /**
     * ==================== 角色权限映射 ====================
     */
    
    /**
     * 管理员角色权限数组
     */
    public static final String[] ADMIN_PERMISSIONS = {
            PERMISSION_USER_MANAGE,
            PERMISSION_USER_VIEW,
            PERMISSION_USER_EDIT,
            PERMISSION_USER_DELETE,
            PERMISSION_PATIENT_MANAGE,
            PERMISSION_PATIENT_VIEW,
            PERMISSION_PATIENT_EDIT,
            PERMISSION_DOCTOR_MANAGE,
            PERMISSION_DOCTOR_VIEW,
            PERMISSION_DOCTOR_EDIT,
            PERMISSION_MEDICAL_RECORD_MANAGE,
            PERMISSION_MEDICAL_RECORD_VIEW,
            PERMISSION_MEDICAL_RECORD_EDIT,
            PERMISSION_PRESCRIPTION_MANAGE,
            PERMISSION_PRESCRIPTION_VIEW,
            PERMISSION_PRESCRIPTION_EDIT,
            PERMISSION_SYSTEM_SETTING,
            PERMISSION_SYSTEM_LOG_VIEW,
            PERMISSION_REPORT_VIEW,
            PERMISSION_REPORT_EXPORT
    };
    
    /**
     * 医生角色权限数组
     */
    public static final String[] DOCTOR_PERMISSIONS = {
            PERMISSION_PATIENT_VIEW,
            PERMISSION_PATIENT_EDIT,
            PERMISSION_MEDICAL_RECORD_MANAGE,
            PERMISSION_MEDICAL_RECORD_VIEW,
            PERMISSION_MEDICAL_RECORD_EDIT,
            PERMISSION_PRESCRIPTION_MANAGE,
            PERMISSION_PRESCRIPTION_VIEW,
            PERMISSION_PRESCRIPTION_EDIT,
            PERMISSION_REPORT_VIEW
    };
    
    /**
     * 患者角色权限数组
     */
    public static final String[] PATIENT_PERMISSIONS = {
            PERMISSION_MEDICAL_RECORD_VIEW,
            PERMISSION_PRESCRIPTION_VIEW
    };
    
    /**
     * 护士角色权限数组
     */
    public static final String[] NURSE_PERMISSIONS = {
            PERMISSION_PATIENT_VIEW,
            PERMISSION_PATIENT_EDIT,
            PERMISSION_MEDICAL_RECORD_VIEW,
            PERMISSION_MEDICAL_RECORD_EDIT
    };
    
    /**
     * 药师角色权限数组
     */
    public static final String[] PHARMACIST_PERMISSIONS = {
            PERMISSION_PRESCRIPTION_VIEW,
            PERMISSION_PRESCRIPTION_EDIT
    };
    
    /**
     * 系统管理员角色权限数组
     */
    public static final String[] SYSTEM_ADMIN_PERMISSIONS = {
            PERMISSION_USER_MANAGE,
            PERMISSION_USER_VIEW,
            PERMISSION_USER_EDIT,
            PERMISSION_USER_DELETE,
            PERMISSION_SYSTEM_SETTING,
            PERMISSION_SYSTEM_LOG_VIEW,
            PERMISSION_REPORT_VIEW,
            PERMISSION_REPORT_EXPORT
    };
    
    /**
     * 普通用户角色权限数组
     */
    public static final String[] USER_PERMISSIONS = {
            PERMISSION_USER_VIEW
    };
    
    /**
     * ==================== JWT相关常量 ====================
     */
    
    /**
     * JWT中角色声明的key
     */
    public static final String JWT_CLAIM_ROLE = "role";
    
    /**
     * JWT中权限声明的key
     */
    public static final String JWT_CLAIM_PERMISSIONS = "permissions";
    
    /**
     * JWT中用户ID声明的key
     */
    public static final String JWT_CLAIM_USER_ID = "userId";
    
    /**
     * JWT中用户名声明的key
     */
    public static final String JWT_CLAIM_USERNAME = "username";
    
    /**
     * ==================== 其他常量 ====================
     */
    
    /**
     * 角色前缀（Spring Security使用）
     */
    public static final String ROLE_PREFIX = "ROLE_";
    
    /**
     * 获取完整的角色名称（带前缀）
     * 
     * @param role 角色名称
     * @return 带前缀的角色名称
     */
    public static String getFullRoleName(String role) {
        if (role == null || role.trim().isEmpty()) {
            return "";
        }
        return ROLE_PREFIX + role.toUpperCase();
    }
    
    /**
     * 检查角色是否为管理员
     * 
     * @param role 角色名称
     * @return 是否为管理员
     */
    public static boolean isAdmin(String role) {
        return ROLE_ADMIN.equalsIgnoreCase(role) || ROLE_SYSTEM_ADMIN.equalsIgnoreCase(role);
    }
    
    /**
     * 检查角色是否为医生
     * 
     * @param role 角色名称
     * @return 是否为医生
     */
    public static boolean isDoctor(String role) {
        return ROLE_DOCTOR.equalsIgnoreCase(role);
    }
    
    /**
     * 检查角色是否为患者
     * 
     * @param role 角色名称
     * @return 是否为患者
     */
    public static boolean isPatient(String role) {
        return ROLE_PATIENT.equalsIgnoreCase(role);
    }
}