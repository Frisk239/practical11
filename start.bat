@echo off
chcp 65001 >nul
echo ========================================
echo    访问监控系统启动器
echo ========================================
echo.

echo [1/4] 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Java环境，请先安装JDK
    pause
    exit /b 1
)
echo Java环境检查通过
echo.

echo [2/4] 设置本地Maven环境...
set "MAVEN_HOME=%~dp0apache-maven-3.9.11"
set "PATH=%MAVEN_HOME%\bin;%PATH%"
echo Maven环境设置完成
echo.

echo [3/4] 启动Spring Boot应用...
cd backend
start "访问监控系统" cmd /c "mvn spring-boot:run"
echo 应用启动中，请稍候...
echo.

echo [4/4] 等待应用启动...
timeout /t 15 /nobreak >nul
echo.

echo [完成] 打开浏览器访问系统...
start http://localhost:8080
echo.
echo ========================================
echo 系统已启动！
echo 访问地址: http://localhost:8080
echo 按任意键关闭此窗口...
echo ========================================
pause >nul
