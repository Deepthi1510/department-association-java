@echo off
REM Build script for Swing GUI application
REM Compiles all Java sources and creates app-swing.jar

setlocal enabledelayedexpansion

REM Create output directory
if not exist out (
    mkdir out
)

echo [*] Compiling Java sources...

REM Compile all packages including swingui
javac -d out ^
    src\com\deptassoc\*.java ^
    src\com\deptassoc\auth\*.java ^
    src\com\deptassoc\dao\*.java ^
    src\com\deptassoc\db\*.java ^
    src\com\deptassoc\dto\*.java ^
    src\com\deptassoc\model\*.java ^
    src\com\deptassoc\ui\*.java ^
    src\com\deptassoc\ui\faculty\*.java ^
    src\com\deptassoc\ui\association\*.java ^
    src\com\deptassoc\util\*.java ^
    src\com\deptassoc\swingui\*.java

if errorlevel 1 (
    echo [ERROR] Compilation failed
    exit /b 1
)

echo [OK] Compilation successful

REM Copy resources
echo [*] Copying resources...
if exist resources (
    xcopy resources out /E /I /Y >nul
)

REM Copy libraries
echo [*] Copying libraries...
if not exist out\lib (
    mkdir out\lib
)
if exist lib (
    xcopy lib\*.jar out\lib\ /Y >nul
)

REM Create JAR file
echo [*] Creating app-swing.jar...
cd out
jar cfe app-swing.jar com.deptassoc.swingui.MainSwing *
cd ..

if exist out\app-swing.jar (
    echo [OK] Build successful!
    echo [INFO] JAR created: out\app-swing.jar
    dir out\app-swing.jar
) else (
    echo [ERROR] JAR creation failed
    exit /b 1
)

echo.
echo Done. Ready to run: run-windows.bat
pause
