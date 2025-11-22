@echo off
REM Build script for Windows
REM Compiles Java sources and creates runnable JAR

setlocal enabledelayedexpansion

REM Set paths
set SRC_DIR=src
set OUT_DIR=out
set LIB_DIR=lib
set RESOURCES_DIR=resources
set CLASSPATH=

REM Build classpath from lib directory
for %%F in (%LIB_DIR%\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%F
)

REM Create output directories
if not exist %OUT_DIR% mkdir %OUT_DIR%
if not exist %OUT_DIR%\lib mkdir %OUT_DIR%\lib

echo Cleaning previous builds...
if exist %OUT_DIR%\*.class del /q %OUT_DIR%\*.class
if exist %OUT_DIR%\com nrmdir /s /q %OUT_DIR%\com

echo Compiling Java sources...
javac -d %OUT_DIR% -cp .;%CLASSPATH% ^
    %SRC_DIR%\com\deptassoc\*.java ^
    %SRC_DIR%\com\deptassoc\db\*.java ^
    %SRC_DIR%\com\deptassoc\dao\*.java ^
    %SRC_DIR%\com\deptassoc\model\*.java ^
    %SRC_DIR%\com\deptassoc\ui\*.java

if errorlevel 1 (
    echo Compilation failed!
    exit /b 1
)

echo Copying configuration files...
xcopy /y %RESOURCES_DIR%\*.properties %OUT_DIR%\

echo Copying library files...
xcopy /y %LIB_DIR%\*.jar %OUT_DIR%\lib\

echo Creating manifest file...
(
    echo Manifest-Version: 1.0
    echo Main-Class: com.deptassoc.Main
    echo Class-Path: lib/*
) > %OUT_DIR%\MANIFEST.MF

echo Creating JAR file...
cd %OUT_DIR%
jar cfm app.jar MANIFEST.MF com\ *.properties
cd ..

echo.
echo Build complete!
echo Runnable JAR created: %OUT_DIR%\app.jar
echo To run: java -jar %OUT_DIR%\app.jar
echo.

endlocal
