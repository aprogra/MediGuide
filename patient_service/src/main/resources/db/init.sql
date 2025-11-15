-- 患者服务数据库初始化脚本
-- 数据库: mediguide_patient

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mediguide_patient DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mediguide_patient;

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码',
    `real_name` varchar(100) DEFAULT NULL COMMENT '真实姓名',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
    `gender` tinyint DEFAULT NULL COMMENT '性别: 0-未知, 1-男, 2-女',
    `birth_date` date DEFAULT NULL COMMENT '出生日期',
    `role` varchar(50) NOT NULL DEFAULT 'PATIENT' COMMENT '角色: ADMIN, DOCTOR, PATIENT, NURSE, PHARMACIST, SYSTEM_ADMIN, USER',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常, 2-锁定, 3-待审核',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
    `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `login_count` int DEFAULT 0 COMMENT '登录次数',
    `remark` text COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` bigint DEFAULT NULL COMMENT '创建人',
    `update_by` bigint DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_status` (`status`),
    KEY `idx_role` (`role`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 患者档案表
CREATE TABLE IF NOT EXISTS `patient_profiles` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '档案ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `patient_no` varchar(50) NOT NULL COMMENT '患者编号',
    `emergency_contact` varchar(100) DEFAULT NULL COMMENT '紧急联系人',
    `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系电话',
    `address` varchar(500) DEFAULT NULL COMMENT '详细地址',
    `medical_history` text COMMENT '既往病史',
    `allergy_history` text COMMENT '过敏史',
    `family_history` text COMMENT '家族史',
    `blood_type` varchar(10) DEFAULT NULL COMMENT '血型',
    `height` decimal(5,2) DEFAULT NULL COMMENT '身高(cm)',
    `weight` decimal(5,2) DEFAULT NULL COMMENT '体重(kg)',
    `occupation` varchar(100) DEFAULT NULL COMMENT '职业',
    `marital_status` tinyint DEFAULT NULL COMMENT '婚姻状况: 0-未婚, 1-已婚, 2-离异, 3-丧偶',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_patient_no` (`patient_no`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者档案表';

-- 初始化管理员用户 (密码: admin123)
INSERT INTO `users` (`username`, `password`, `real_name`, `email`, `phone`, `role`, `status`, `deleted`, `create_by`, `update_by`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '系统管理员', 'admin@mediguide.com', '13800138000', 'SYSTEM_ADMIN', 1, 0, 1, 1);

-- 初始化测试患者用户 (密码: patient123)
INSERT INTO `users` (`username`, `password`, `real_name`, `email`, `phone`, `id_card`, `gender`, `birth_date`, `role`, `status`, `deleted`, `create_by`, `update_by`) VALUES
('patient01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '张三', 'patient01@mediguide.com', '13900139000', '110101199001011234', 1, '1990-01-01', 'PATIENT', 1, 0, 1, 1),
('patient02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '李四', 'patient02@mediguide.com', '14000140000', '110101199002022345', 2, '1990-02-02', 'PATIENT', 1, 0, 1, 1);

-- 初始化测试医生用户 (密码: doctor123)
INSERT INTO `users` (`username`, `password`, `real_name`, `email`, `phone`, `role`, `status`, `deleted`, `create_by`, `update_by`) VALUES
('doctor01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '王医生', 'doctor01@mediguide.com', '15000150000', 'DOCTOR', 1, 0, 1, 1),
('doctor02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '李医生', 'doctor02@mediguide.com', '16000160000', 'DOCTOR', 1, 0, 1, 1);

-- 初始化测试护士用户 (密码: nurse123)
INSERT INTO `users` (`username`, `password`, `real_name`, `email`, `phone`, `role`, `status`, `deleted`, `create_by`, `update_by`) VALUES
('nurse01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '张护士', 'nurse01@mediguide.com', '17000170000', 'NURSE', 1, 0, 1, 1);

-- 初始化测试药师用户 (密码: pharmacist123)
INSERT INTO `users` (`username`, `password`, `real_name`, `email`, `phone`, `role`, `status`, `deleted`, `create_by`, `update_by`) VALUES
('pharmacist01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', '陈药师', 'pharmacist01@mediguide.com', '18000180000', 'PHARMACIST', 1, 0, 1, 1);

-- 创建患者档案
INSERT INTO `patient_profiles` (`user_id`, `patient_no`, `emergency_contact`, `emergency_phone`, `address`, `medical_history`, `allergy_history`, `blood_type`, `height`, `weight`, `occupation`, `marital_status`) VALUES
(2, 'P20240001', '张父', '13900139001', '北京市朝阳区某某街道某某小区1号楼1单元101室', '无重大病史', '无过敏史', 'A型', 175.00, 70.00, '软件工程师', 1),
(3, 'P20240002', '李母', '14000140001', '北京市海淀区某某街道某某小区2号楼2单元202室', '轻度高血压', '青霉素过敏', 'B型', 165.00, 55.00, '教师', 0);

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_patient_profiles_user_id ON patient_profiles(user_id);
CREATE INDEX idx_patient_profiles_patient_no ON patient_profiles(patient_no);