# 访问监控系统 - 部署说明

## 系统简介
这是一个基于Spring Boot和H2数据库的访问监控系统，使用动态数组和指针实现用户会话管理。

## 环境要求
- **Java JDK**: 17或更高版本
- **操作系统**: Windows 10/11
- **浏览器**: Chrome、Firefox、Edge等现代浏览器

## 目录结构
```
访问监控系统/
├── apache-maven-3.9.11/     # 本地Maven环境
├── backend/                 # Spring Boot后端
│   ├── src/
│   ├── pom.xml
│   └── target/
├── frontend/                # 前端静态文件
│   ├── index.html
│   ├── user-portal.html
│   ├── css/
│   └── js/
├── start.bat               # 一键启动脚本
└── README_部署说明.md      # 本说明文件
```

## 快速启动

### 方法一：一键启动（推荐）
1. 双击 `start.bat` 文件
2. 脚本会自动：
   - 检查Java环境
   - 配置本地Maven环境
   - 启动Spring Boot应用
   - 等待应用启动完成
   - 自动打开浏览器访问系统

### 方法二：手动启动
如果一键启动脚本有问题，可以手动执行：

1. 打开命令提示符，进入项目根目录
2. 设置Maven环境：
   ```cmd
   set MAVEN_HOME=%cd%\apache-maven-3.9.11
   set PATH=%MAVEN_HOME%\bin;%PATH%
   ```
3. 进入backend目录：
   ```cmd
   cd backend
   ```
4. 启动应用：
   ```cmd
   mvn spring-boot:run
   ```
5. 应用启动后，在浏览器中访问：http://localhost:8080

## 功能说明

### 管理后台 (http://localhost:8080)
- 用户注册管理
- 访问历史查看
- 系统统计信息

### 用户门户 (http://localhost:8080/user-portal.html)
- 用户登录/登出
- 个人访问记录查看

## 技术栈
- **后端**: Spring Boot 3.2.0, Spring Data JPA
- **数据库**: H2 Database (文件模式)
- **前端**: HTML5, CSS3, JavaScript (ES6)
- **构建工具**: Apache Maven 3.9.11

## 数据存储
系统使用H2数据库，数据文件存储在 `backend/data/` 目录下：
- `access_monitoring.mv.db`: 数据库文件
- `access_monitoring.trace.db`: 数据库日志文件

## 故障排除

### 启动失败
1. 确认已安装Java JDK
2. 确认端口8080未被其他程序占用
3. 查看命令行输出的错误信息

### 浏览器无法访问
1. 确认应用已完全启动（等待Spring Boot启动完成）
2. 检查防火墙设置
3. 尝试使用其他浏览器

### Maven相关问题
1. 确认Maven环境变量设置正确
2. 删除 `backend/target/` 目录后重新启动

## 停止应用
在运行Spring Boot应用的命令行窗口中按 `Ctrl+C` 停止应用。

## 注意事项
- 首次启动会自动创建数据库表和初始化数据
- 系统会在8080端口运行，请确保该端口可用
- 数据会持久化保存，重启应用后数据仍然存在
