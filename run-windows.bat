@echo off
REM Run script for Windows
REM Runs the application using JAR file

setlocal

if not exist out\app.jar (
    echo ERROR: out\app.jar not found!
    echo Please run build-windows.bat first.
    exit /b 1
)

echo Starting Department Association Management System...
echo.

java -jar out\app.jar

endlocal
