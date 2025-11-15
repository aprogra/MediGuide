# 患者服务模块 (patient-service)

MediGuide医疗导航系统的患者服务模块，提供用户认证、授权和患者管理功能。

## 功能特性
- 用户登录认证 (JWT)
- 角色权限管理
- 患者档案管理
- 多角色支持 (患者、医生、护士、药师、管理员)
- Swagger API文档

## 技术栈
- **Spring Boot**: 3.2.0
- **Spring Security**: 认证授权框架
- **JWT**: 无状态认证
- **MyBatis Plus**: ORM框架
- **MySQL**: 8.0+ 数据库
- **Redis**: 缓存和会话管理
- **Swagger**: API文档

## 快速开始

### 1. 环境要求
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 数据库初始化
```sql
-- 创建数据库
CREATE DATABASE mediguide_patient DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行初始化脚本
mysql -u root -p mediguide_patient < patient_service/src/main/resources/db/init.sql
```

### 3. 配置文件
编辑 `src/main/resources/application.yml` 文件，配置数据库和Redis连接。

### 4. 运行项目
```bash
mvn clean spring-boot:run
```

### 5. API文档
启动后访问：http://localhost:8081/swagger-ui.html

## 测试用户
| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | SYSTEM_ADMIN | 系统管理员 |
| patient01 | patient123 | PATIENT | 测试患者 |
| doctor01 | doctor123 | DOCTOR | 测试医生 |
| nurse01 | nurse123 | NURSE | 测试护士 |
| pharmacist01 | pharmacist123 | PHARMACIST | 测试药师 |

## 项目结构
```
patient_service/
├── src/main/java/com/mediguide/patient/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器
│   ├── entity/          # 实体类
│   ├── mapper/          # 数据访问层
│   ├── security/        # 安全相关
│   ├── service/         # 服务层
│   └── utils/           # 工具类
├── src/main/resources/
│   ├── db/init.sql      # 数据库初始化脚本
│   └── application.yml  # 配置文件
└── pom.xml              # Maven配置
```

## 核心API

### 认证接口
- **登录**: POST `/api/patient/auth/login`
- **登出**: POST `/api/patient/auth/logout`
- **刷新令牌**: POST `/api/patient/auth/refresh`
- **验证令牌**: GET `/api/patient/auth/validate`

### 用户管理
- **获取用户信息**: GET `/api/patient/auth/user-info`
- **修改密码**: PUT `/api/patient/auth/change-password`

## 安全特性
- JWT无状态认证
- BCrypt密码加密
- 基于角色的访问控制(RBAC)
- CORS跨域处理
- SQL注入防护

---

# MediGuide Parent Module - 父模块

MediGuide辅助诊疗助手系统的Maven多模块父项目，统一管理依赖版本和构建配置。

## 模块结构
- **patient_service**: 患者服务模块 (用户认证、患者管理)
- **doctor_client**: 医生端服务模块
- **AI_server**: AI引擎服务模块

## 技术栈
- **构建工具**: Maven 3.9
- **Java版本**: 17
- **Spring Boot**: 3.4.5
- **依赖管理**: 集中管理所有子模块依赖版本

## 依赖版本管理
```xml
<properties>
    <spring-boot.version>3.4.5</spring-boot.version>
    <mybatis-plus.version>3.5.10</mybatis-plus.version>
    <spring-ai.version>1.0.3</spring-ai.version>
    <mysql.version>8.0.33</mysql.version>
</properties>
```

## 主要依赖
- **Spring Boot**: 核心框架
- **MyBatis Plus**: ORM框架
- **Spring AI**: AI引擎集成
- **MySQL**: 数据库驱动

## 构建命令
```bash
# 清理并编译所有模块
mvn clean install

# 跳过测试构建
mvn clean install -DskipTests

# 打包所有模块
mvn clean package
```

## 模块依赖关系
```
patient_client (父模块)
├── doctor_client (医生端服务)
└── AI_server (AI引擎服务)
```

## 配置说明
- 所有子模块自动继承父模块的依赖版本
- 统一编码格式：UTF-8
- 统一Java编译版本：17
- 提供Spring Boot Maven插件统一管理