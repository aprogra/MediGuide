# MediGuide Common Module - 公共模块

公共模块是MediGuide系统的核心基础库，提供通用的工具类、常量定义、统一响应格式和异常处理机制，被所有其他业务模块依赖。

## 模块定位
- **功能定位**: 系统通用功能库，提供可复用的基础组件
- **依赖关系**: 被patient-service、doctor-service、ai-service等业务模块依赖
- **设计原则**: 高内聚、低耦合、无业务逻辑、纯工具性质

## 技术依赖
```xml
<dependencies>
    <!-- Lombok - 简化Java代码开发 -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
    
    <!-- Hutool - Java工具包，提供丰富的工具类 -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
    </dependency>
</dependencies>
```

## 目录结构
```
common/
├── src/main/java/com/mediguide/common/
│   ├── entity/           # 通用实体类
│   │   └── Result.java   # 统一响应结果实体
│   ├── utils/            # 工具类集合
│   │   ├── JwtUtils.java # JWT令牌工具类
│   │   ├── EncryptUtils.java # 加密解密工具类
│   │   └── DateUtils.java    # 日期时间工具类
│   ├── constant/         # 常量定义
│   │   ├── RoleConstant.java # 角色权限常量
│   │   ├── StatusConstant.java # 状态码常量
│   │   └── ConfigConstant.java # 配置键常量
│   └── exception/        # 异常处理
│       ├── BusinessException.java  # 自定义业务异常
│       └── GlobalExceptionHandler.java # 全局异常处理器
```

## 核心功能

### 1. 统一响应格式 (Result)
提供标准化的API响应格式，包含状态码、消息、数据等字段。

```java
// 成功响应
Result.success(data);
Result.success("操作成功", data);

// 失败响应
Result.error("错误信息");
Result.error(500, "服务器错误");
```

### 2. JWT工具类 (JwtUtils)
提供JWT令牌的生成、解析、验证等功能，支持用户认证和授权。

```java
// 生成令牌
String token = JwtUtils.generateToken(userId, username, role);

// 解析令牌
Claims claims = JwtUtils.parseToken(token);

// 验证令牌
boolean isValid = JwtUtils.validateToken(token);
```

### 3. 加密工具类 (EncryptUtils)
提供密码加密、数据加密解密等安全相关功能。

```java
// 密码加密（BCrypt）
String encryptedPassword = EncryptUtils.encryptPassword(rawPassword);

// 密码验证
boolean isMatch = EncryptUtils.verifyPassword(rawPassword, encryptedPassword);

// 数据加密解密
String encryptedData = EncryptUtils.encryptData(data, secretKey);
String decryptedData = EncryptUtils.decryptData(encryptedData, secretKey);
```

### 4. 角色权限常量 (RoleConstant)
定义系统中使用的角色和权限常量，确保一致性。

```java
public class RoleConstant {
    public static final String ROLE_PATIENT = "PATIENT";      // 患者角色
    public static final String ROLE_DOCTOR = "DOCTOR";        // 医生角色
    public static final String ROLE_ADMIN = "ADMIN";          // 管理员角色
    
    public static final String AUTHORITY_PREFIX = "ROLE_";    // 权限前缀
}
```

### 5. 业务异常处理 (BusinessException)
提供自定义业务异常类，支持错误码和错误信息。

```java
// 抛出业务异常
throw new BusinessException("用户名已存在");
throw new BusinessException(1001, "账户已被锁定");

// 全局异常处理器自动捕获并返回统一响应
@ExceptionHandler(BusinessException.class)
public Result handleBusinessException(BusinessException e) {
    return Result.error(e.getCode(), e.getMessage());
}
```

## 使用规范

### 1. 依赖管理
- 所有业务模块必须通过Maven依赖引入common模块
- 禁止在common模块中引入业务相关的依赖
- 保持依赖的轻量级和通用性

### 2. 编码规范
- 工具类必须使用final修饰，防止被继承
- 常量类必须包含私有构造函数，防止实例化
- 所有公共方法必须有完整的JavaDoc注释
- 遵循统一的命名规范和代码风格

### 3. 版本管理
- common模块的版本变更必须谨慎，确保向后兼容性
- 新增功能时，必须保证不影响现有功能
- 修改现有功能时，必须经过充分测试

## 开发计划
- **第一阶段**: 完善基础工具类（JWT、加密、日期处理）
- **第二阶段**: 添加更多通用工具（文件处理、数据验证、缓存工具）
- **第三阶段**: 优化异常处理机制，提供更多自定义异常类型

## 注意事项
1. 禁止在common模块中添加任何业务逻辑
2. 所有工具类必须保证线程安全性
3. 对外暴露的API必须保持稳定，避免频繁变更
4. 新增功能前，先检查是否已存在类似功能，避免重复开发