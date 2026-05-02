#!/bin/bash

echo "================================================"
echo " Secure File Management System"
echo "================================================"
echo ""

if [ ! -d "bin" ]; then
    echo "ERROR: Compiled classes not found!"
    echo "Please run ./compile.sh first."
    echo ""
    exit 1
fi

echo "Starting application..."
echo ""
java -cp bin com.securefiles.main.SecureFileManagementSystem

echo ""
echo "================================================"
echo " Application Closed"
echo "================================================"
