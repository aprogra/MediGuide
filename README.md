[text](README.md)# MediGuide

辅助诊疗助手系统 - 基于Spring Boot + Vue3 + Spring AI的智能化医疗辅助平台

## 系统概述
MediGuide是一个现代化的辅助诊疗助手系统，旨在为医生提供智能化的诊疗建议和药方推荐服务，提高诊疗效率和准确性。

## 核心功能

### 1. 辅助诊疗功能
**第一阶段 - 初步诊疗方案生成**
- 输入：患者简述（医生端填写）
- 处理：提取有用信息 → 分析病情 → 生成初步诊断
- 输出：现病史询问方向 + 初步病情诊断
- 技术特点：智能提示词工程，可扩展的信息提取和分析框架

**第二阶段 - 详细诊疗结果生成**
- 输入：初步诊断 + 往期病历 + 现病史 + 化验报告 + 其他信息（检查结果、医生补充、图片数据）
- 处理：多源数据融合分析 → 权重平衡 → 快速响应
- 输出：详细诊疗结果
- 技术特点：多模态数据处理，优化的响应算法

### 2. 辅助药方功能
- 智能药品推荐系统
- 硬编程迷你药房管理
- 药效推理引擎
- 副作用和药物冲突检查
- 药理分析报告

## 技术架构
- **后端**: Spring Boot 3.4.5, Java 17, Maven 3.9
- **AI引擎**: Spring AI 1.0.3
- **数据存储**: MySQL 8.0, MyBatis Plus
- **前端**: Vue3 + Vue CLI + Element Plus
- **多模态处理**: 文本、图像、结构化数据

## 项目结构
```
MediGuide/
├── patient-service/           # 患者服务模块
│   ├── pom.xml               # 患者服务依赖配置
│   ├── README.md             # 患者服务说明
│   └── src/
│       └── main/java/com/medicguide/patient/
│           ├── controller/   # 患者控制器
│           ├── service/      # 患者业务逻辑
│           ├── entity/       # 患者实体类
│           ├── mapper/       # 患者数据访问
│           └── dto/          # 患者数据传输对象
├── doctor-service/            # 医生服务模块
│   ├── pom.xml               # 医生服务依赖配置
│   ├── README.md             # 医生服务说明
│   └── src/
│       └── main/java/com/medicguide/doctor/
│           ├── controller/   # 医生控制器
│           ├── service/      # 医生业务逻辑
│           ├── entity/       # 医生实体类
│           ├── mapper/       # 医生数据访问
│           └── dto/          # 医生数据传输对象
├── ai-service/                # AI引擎服务模块
│   ├── pom.xml               # AI引擎服务依赖配置
│   ├── README.md             # AI引擎服务说明
│   └── src/
│       └── main/java/com/medicguide/ai/
│           ├── controller/   # AI服务控制器
│           ├── service/      # AI业务逻辑
│           ├── prompt/       # 提示词管理
│           ├── parser/       # 结果解析器
│           ├── multimodal/   # 多模态处理
│           ├── entity/       # AI相关实体
│           └── dto/          # AI数据传输对象
├── view/                      # Vue3前端项目
│   ├── package.json          # 前端依赖配置
│   ├── README.md             # 前端项目说明
│   ├── vue.config.js         # Vue CLI配置
│   └── src/
│       ├── components/       # Vue组件
│       ├── views/            # 页面视图
│       ├── router/           # 路由配置
│       ├── store/            # 状态管理
│       └── api/              # API接口调用
├── common/                    # 公共模块 - 系统通用功能库
│   ├── pom.xml               # 公共依赖配置（Lombok、Hutool工具包）
│   ├── README.md             # 公共模块详细说明文档
│   └── src/
│       └── main/java/com/medicguide/common/
│           ├── entity/       # 通用响应实体（Result统一响应格式）
│           ├── utils/        # 工具类集合（JWT工具、加密解密、日期处理）
│           ├── constant/     # 系统常量定义（权限角色、状态码、配置键）
│           └── exception/    # 异常处理（自定义业务异常、全局异常处理器）
└── docs/                     # 项目文档
    └── ARCHITECTURE.md       # 系统架构文档
```

## 开发计划
1. **第一阶段**: 基础框架搭建 + 初步诊疗功能
2. **第二阶段**: 详细诊疗功能 + 多模态数据处理
3. **第三阶段**: 辅助药方功能 + 系统集成测试

## 版本信息
- 当前版本: v1.0.0
- 开发状态: 架构设计阶段
