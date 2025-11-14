# MediGuide Frontend - 辅助诊疗助手系统前端

基于 Vue3 + Vue CLI + Element Plus 的现代化医疗辅助诊疗系统前端

## 技术栈
- **框架**: Vue 3.4.21
- **路由**: Vue Router 4.3.0
- **状态管理**: Pinia 2.1.7
- **UI组件库**: Element Plus 2.6.1
- **图表**: ECharts 5.5.0 + Vue ECharts 6.6.1
- **HTTP客户端**: Axios 1.6.8
- **富文本编辑器**: Quill 1.3.7
- **图片裁剪**: Vue3 Cropper 1.0.3

## 功能模块
- **诊疗流程页面**: 辅助诊疗两阶段流程界面
- **药方管理页面**: 智能药方推荐和管理
- **患者信息管理**: 患者档案和历史记录
- **多模态数据展示**: 支持文本、图像、报告等多种数据格式
- **实时诊断建议**: AI辅助诊断结果展示
- **药方推荐**: 智能药品推荐和冲突检查

## 项目结构
```
view/
├── public/                    # 静态资源
├── src/
│   ├── components/           # 公共组件
│   │   ├── diagnosis/        # 诊疗相关组件
│   │   ├── prescription/     # 药方相关组件
│   │   └── common/           # 通用组件
│   ├── views/                # 页面视图
│   │   ├── Dashboard.vue     # 仪表板
│   │   ├── Diagnosis.vue     # 诊疗流程页面
│   │   ├── Prescription.vue  # 药方管理页面
│   │   └── Patient.vue       # 患者管理页面
│   ├── router/               # 路由配置
│   ├── store/                # 状态管理
│   ├── api/                  # API接口
│   ├── utils/                # 工具函数
│   └── assets/               # 静态资源
└── package.json
```

## 开发环境
```bash
# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 构建生产版本
npm run build

# 代码检查
npm run lint
```

## 配置说明
- 端口: 默认8080
- 代理配置: 开发环境代理到后端服务
- 主题定制: 基于Element Plus的医疗主题

## 开发规范
- 使用ESLint进行代码检查
- 遵循Vue 3 Composition API规范
- 组件命名采用PascalCase
- 文件命名采用kebab-case
