@echo off
REM Run script for Swing GUI application
REM Launches the Swing application with proper classpath

setlocal enabledelayedexpansion

if not exist out\app-swing.jar (
    echo [ERROR] app-swing.jar not found. Please run build-swing-windows.bat first.
    exit /b 1
)

REM Build classpath
set CLASSPATH=out;out\lib\*;out\config.properties

echo [*] Starting Swing GUI application...
echo [INFO] Classpath: %CLASSPATH%

java -cp "%CLASSPATH%" com.deptassoc.swingui.MainSwing

if errorlevel 1 (
    echo [ERROR] Application failed to start
    pause
    exit /b 1
)
