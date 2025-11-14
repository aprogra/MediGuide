# MediGuide Parent Module - 父模块

MediGuide辅助诊疗助手系统的Maven多模块父项目，统一管理依赖版本和构建配置。

## 模块结构
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