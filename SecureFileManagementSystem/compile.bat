@echo off
echo ================================================
echo  Secure File Management System - Compilation
echo ================================================
echo.

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

echo Compiling Java files...
javac -d bin -sourcepath src src\com\securefiles\main\SecureFileManagementSystem.java

if %errorlevel% equ 0 (
    echo.
    echo ================================================
    echo  Compilation Successful!
    echo ================================================
    echo.
    echo To run the application, execute: run.bat
    echo.
) else (
    echo.
    echo ================================================
    echo  Compilation Failed!
    echo ================================================
    echo Please check the error messages above.
    echo.
)

pause
