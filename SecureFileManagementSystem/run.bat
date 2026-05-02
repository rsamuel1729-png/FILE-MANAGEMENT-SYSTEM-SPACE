@echo off
echo ================================================
echo  Secure File Management System
echo ================================================
echo.

if not exist bin (
    echo ERROR: Compiled classes not found!
    echo Please run compile.bat first.
    echo.
    pause
    exit
)

echo Starting application...
echo.
java -cp bin com.securefiles.main.SecureFileManagementSystem

echo.
echo ================================================
echo  Application Closed
echo ================================================
pause
