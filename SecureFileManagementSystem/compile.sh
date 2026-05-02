#!/bin/bash

echo "================================================"
echo " Secure File Management System - Compilation"
echo "================================================"
echo ""

# Create bin directory if it doesn't exist
mkdir -p bin

echo "Compiling Java files..."
javac -d bin -sourcepath src src/com/securefiles/main/SecureFileManagementSystem.java

if [ $? -eq 0 ]; then
    echo ""
    echo "================================================"
    echo " Compilation Successful!"
    echo "================================================"
    echo ""
    echo "To run the application, execute: ./run.sh"
    echo ""
else
    echo ""
    echo "================================================"
    echo " Compilation Failed!"
    echo "================================================"
    echo "Please check the error messages above."
    echo ""
fi
