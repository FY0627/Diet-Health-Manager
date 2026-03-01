# 🍏 饮食健康管理系统 (Diet Health Manager)

本项目是一款基于 **Spring Boot 3** 和 **Vue 3** 开发的个人健康管理平台。旨在帮助用户通过记录每日饮食、设定营养目标、监控身体指标（BMI/BMR），实现科学自律的健康生活。

## 🌟 核心功能

### 1. 用户管理

- **安全认证**：基于 Session 的登录态管理。
- **密码安全**：采用 MD5 加盐加密存储，确保数据库信息安全。
- **个人中心**：支持生日、性别等基础资料的完善与修改。

### 2. 饮食记录与统计

- **智能记录**：支持从公共食物库搜索并记录饮食，自动换算克数对应的营养成分。
- **数据看板**：可视化展示每日卡路里、蛋白质、碳水、脂肪的摄入进度。
- **餐别分析**：细化统计早餐、午餐、晚餐、加餐的热量分布。
- **智能建议**：根据剩余热量额度提供实时健康提醒。

### 3. 食物库管理

- **分页检索**：支持对海量食物数据进行模糊搜索与分页展示。
- **自定义食物**：支持用户录入私人食谱进入系统字典。

### 4. 健康数据监测

- **指标计算**：录入身高体重后，系统自动计算 **BMI**（体质指数）与 **BMR**（基础代谢率）。
- **目标对比**：支持自定义每日营养目标，并在看板中实时对比达标情况。

## 🛠️ 技术栈

### 后端 (Backend)

- **核心框架**：Spring Boot 3.3.0
- **持久层**：MyBatis Plus (集成分页插件、自动填充拦截器)
- **数据库**：MySQL 8.0
- **工具类**：Hutool (用于 MD5 加密)、Lombok
- **规范**：统一 Result 结果封装、全局异常处理 (GlobalExceptionHandler)

### 前端 (Frontend)

- **核心框架**：Vue 3 (Composition API)
- **构建工具**：Vite
- **UI 组件库**：Element Plus
- **路由管理**：Vue Router
- **网络请求**：Axios (集成请求/响应拦截器、跨域 Proxy 配置)

## 📂 项目结构

Plaintext

```
diet-health-manager/
├── diet-health-manager-backend/    # 后端工程
│   ├── src/main/java/.../config/   # MyBatis Plus 配置
│   ├── src/main/java/.../controller/# 接口层
│   ├── src/main/java/.../service/   # 业务层 (包含复杂的营养换算逻辑)
│   └── src/main/java/.../model/vo/  # 聚合统计 VO 视图对象
├── diet-health-manager-frontend/   # 前端工程
│   ├── src/views/                  # 包含看板、记录、食物库等所有页面
│   ├── src/utils/request.js        # 封装 Axios 请求
│   └── vite.config.js              # 反向代理配置
└── diet-health-manager-sql/        # 数据库初始化脚本
```

## 🚀 快速开始

### 1. 数据库准备

执行 `diet-health-manager-sql/schema.sql` 中的脚本，创建数据库表结构并初始化食物数据。

### 2. 后端启动

1. 修改 `application.yml` 中的数据库连接信息（用户名及密码）。
2. 运行 `DietHealthManagerApplication` 启动类。
3. 后端默认运行在 `http://localhost:8080`。

### 3. 前端启动

Bash

```
cd diet-health-manager-frontend
npm install
npm run dev
```

访问 `http://localhost:5173` 即可进入系统。

------
