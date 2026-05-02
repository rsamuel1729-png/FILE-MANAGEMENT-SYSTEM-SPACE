# Quick Start Guide - Secure File Management System

## For Windows Users

### Step 1: Extract the ZIP file
- Extract `SecureFileManagementSystem.zip` to your desired location
- Example: `C:\Projects\SecureFileManagementSystem`

### Step 2: Open in VS Code
1. Open VS Code
2. File → Open Folder
3. Select the `SecureFileManagementSystem` folder
4. Install "Extension Pack for Java" if prompted

### Step 3: Compile and Run
**Option A - Using Scripts:**
1. Double-click `compile.bat` to compile
2. Double-click `run.bat` to run

**Option B - Using VS Code:**
1. Open `SecureFileManagementSystem.java` from `src/com/securefiles/main/`
2. Click the "Run" button (▶) at the top-right
3. Or press F5

### Step 4: Login
Use one of these credentials:
- Username: `john`, Password: `pass123` (Regular User)
- Username: `admin`, Password: `admin123` (Administrator)

---

## For Linux/Mac Users

### Step 1: Extract the ZIP file
```bash
unzip SecureFileManagementSystem.zip
cd SecureFileManagementSystem
```

### Step 2: Make scripts executable
```bash
chmod +x compile.sh run.sh
```

### Step 3: Compile and Run
**Option A - Using Scripts:**
```bash
./compile.sh
./run.sh
```

**Option B - Using VS Code:**
1. Open VS Code: `code .`
2. Install "Extension Pack for Java" if prompted
3. Open `SecureFileManagementSystem.java` from `src/com/securefiles/main/`
4. Click the "Run" button or press F5

### Step 4: Login
Use one of these credentials:
- Username: `john`, Password: `pass123` (Regular User)
- Username: `admin`, Password: `admin123` (Administrator)

---

## Manual Compilation (All Platforms)

If scripts don't work, use these commands:

### Compile:
```bash
# Create bin directory
mkdir bin

# Compile (Windows)
javac -d bin -sourcepath src src\com\securefiles\main\SecureFileManagementSystem.java

# Compile (Linux/Mac)
javac -d bin -sourcepath src src/com/securefiles/main/SecureFileManagementSystem.java
```

### Run:
```bash
# Run (All platforms)
java -cp bin com.securefiles.main.SecureFileManagementSystem
```

---

## Testing the Application

### Basic Flow:
1. **Login** with john/pass123
2. **Upload a file** (Option 1)
   - Filename: `test.txt`
   - Content: `Hello World`
3. **View files** (Option 2)
4. **Encrypt file** (Option 5)
   - File ID: `F001`
5. **View file content** (Option 3)
   - See encrypted content
6. **Decrypt file** (Option 6)
7. **View audit logs** (Option 14)
8. **Logout** (Option 16)

---

## Project Structure

```
SecureFileManagementSystem/
├── src/                    (Source code)
│   └── com/securefiles/
│       ├── interfaces/     (3 interfaces)
│       ├── exceptions/     (3 exceptions)
│       ├── models/         (7 model classes)
│       ├── services/       (4 service classes)
│       └── main/          (1 main class)
├── bin/                    (Compiled .class files)
├── .vscode/               (VS Code configuration)
├── compile.bat/sh         (Compilation scripts)
├── run.bat/sh            (Execution scripts)
├── README.md             (Project overview)
├── DOCUMENTATION.md      (Technical documentation)
└── QUICKSTART.md         (This file)
```

---

## Common Issues & Solutions

### Issue 1: "javac not found"
**Solution:** Install Java JDK
- Download from: https://www.oracle.com/java/technologies/downloads/
- Or install OpenJDK: https://adoptium.net/

### Issue 2: "Class not found" when running
**Solution:** Ensure you're in the project root directory
```bash
cd SecureFileManagementSystem
java -cp bin com.securefiles.main.SecureFileManagementSystem
```

### Issue 3: VS Code doesn't recognize Java
**Solution:** Install "Extension Pack for Java"
- Press Ctrl+Shift+X
- Search for "Extension Pack for Java"
- Click Install

### Issue 4: Compilation errors
**Solution:** Check Java version
```bash
java -version    # Should be 8 or higher
javac -version   # Should match Java version
```

### Issue 5: Permission denied on Linux/Mac
**Solution:** Make scripts executable
```bash
chmod +x compile.sh run.sh
```

---

## Features to Test

- ✅ User Authentication
- ✅ File Upload/Download
- ✅ File Encryption/Decryption
- ✅ File Deletion
- ✅ Audit Logging
- ✅ Session Management
- ✅ Access Control
- ✅ File Metadata Display
- ✅ User Profile Display
- ✅ System Configuration Display
- ✅ Disk I/O Operations

---

## Next Steps

1. Try all menu options
2. Test with both user accounts (john and admin)
3. Create multiple files
4. Review audit logs
5. Read technical documentation (DOCUMENTATION.md)
6. Explore the source code

---

## Support

For issues or questions:
1. Check README.md for detailed information
2. Check DOCUMENTATION.md for technical details
3. Review the source code comments
4. Consult course instructor/TA

---

**Happy Coding! 🎓**
