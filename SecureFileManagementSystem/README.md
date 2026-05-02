# Secure File Management System

A comprehensive Java-based secure file management system demonstrating Object-Oriented Programming concepts.

## Project Structure

```
SecureFileManagementSystem/
├── src/
│   └── com/
│       └── securefiles/
│           ├── interfaces/          (3 interfaces)
│           │   ├── Authenticatable.java
│           │   ├── Encryptable.java
│           │   └── Auditable.java
│           ├── exceptions/          (3 custom exceptions)
│           │   ├── AuthenticationException.java
│           │   ├── FileOperationException.java
│           │   └── AccessDeniedException.java
│           ├── models/              (7 model classes)
│           │   ├── Person.java
│           │   ├── User.java
│           │   ├── Admin.java
│           │   ├── FileEntity.java
│           │   ├── Session.java
│           │   ├── AccessControl.java
│           │   └── SystemConfig.java
│           ├── services/            (4 service classes)
│           │   ├── FileManager.java
│           │   ├── EncryptionProfile.java
│           │   ├── KeyManager.java
│           │   └── AuditLog.java
│           └── main/                (1 main class)
│               └── SecureFileManagementSystem.java
├── bin/                             (compiled classes)
└── README.md
```

## OOP Concepts Implemented

1. **Abstraction** - Person abstract class, Interfaces
2. **Encapsulation** - Private attributes with getters/setters
3. **Inheritance** - User extends Person, Admin extends User
4. **Polymorphism** - Interface implementation, method overriding
5. **Interfaces** - Authenticatable, Encryptable, Auditable
6. **Association** - FileManager ↔ FileEntity
7. **Aggregation** - Session has User
8. **Composition** - FileManager contains List<FileEntity>
9. **Exception Handling** - Custom exceptions throughout
10. **Collections Framework** - ArrayList, List interfaces

## Features

- User authentication and authorization
- Secure file upload, download, delete operations
- File encryption/decryption (Caesar cipher simulation)
- Access control management
- Audit logging for all operations
- Session management
- File metadata tracking
- Disk I/O operations
- Comprehensive exception handling

## Default Users

1. **Regular User**
   - Username: `john`
   - Password: `pass123`

2. **Administrator**
   - Username: `admin`
   - Password: `admin123`

## Compilation Instructions

### Option 1: Using Command Line (Windows)

```bash
# Navigate to project directory
cd SecureFileManagementSystem

# Create bin directory
mkdir bin

# Compile all Java files
javac -d bin -sourcepath src src/com/securefiles/main/SecureFileManagementSystem.java
```

### Option 2: Using Command Line (Linux/Mac)

```bash
# Navigate to project directory
cd SecureFileManagementSystem

# Create bin directory
mkdir -p bin

# Compile all Java files
javac -d bin -sourcepath src src/com/securefiles/main/SecureFileManagementSystem.java
```

### Option 3: Using VS Code

1. Open the `SecureFileManagementSystem` folder in VS Code
2. Install "Extension Pack for Java" if not already installed
3. VS Code will automatically detect the project structure
4. Click the "Run" button or press F5 to compile and run

## Execution Instructions

### Using Command Line

```bash
# From project root directory
cd SecureFileManagementSystem

# Run the application
java -cp bin com.securefiles.main.SecureFileManagementSystem
```

### Using VS Code

1. Open `SecureFileManagementSystem.java` in the editor
2. Right-click in the editor and select "Run Java"
3. Or click the "Run" button in the top-right corner

## Menu Options

1. **Upload File** - Upload a new file with content
2. **View All Files** - List all files owned by current user
3. **Download File** - View file content
4. **Delete File** - Remove a file
5. **Encrypt File** - Encrypt file content (Caesar cipher)
6. **Decrypt File** - Decrypt encrypted file
7. **View File Metadata** - Display complete file information
8. **View User Profile** - Display user/admin profile details
9. **View Session Info** - Display current session information
10. **View File Manager Status** - Show storage statistics
11. **View Encryption Profile** - Display encryption settings
12. **View Key Manager Info** - Show encryption key details
13. **View Access Control Info** - Display file permissions
14. **View Audit Logs** - Show all system activities
15. **Read File from Disk** - Read actual file from disk
16. **Logout** - End session and exit

## Sample Usage Flow

```
1. Login with username: john, password: pass123
2. Choose option 1 (Upload File)
3. Enter filename: document.txt
4. Enter content: This is a test file
5. Choose option 2 (View All Files) to see uploaded file
6. Choose option 5 (Encrypt File) and enter file ID: F001
7. Choose option 3 (Download File) to see encrypted content
8. Choose option 6 (Decrypt File) to decrypt
9. Choose option 14 (View Audit Logs) to see all activities
10. Choose option 16 (Logout) to exit
```

## Exception Handling

The system implements comprehensive exception handling:

- **AuthenticationException** - Login failures, account lockout
- **FileOperationException** - File I/O errors, file not found
- **AccessDeniedException** - Unauthorized access attempts
- **InputMismatchException** - Invalid menu inputs
- **NullPointerException** - Null safety checks
- **IOException** - Disk read/write errors

## Collections Used

- **ArrayList<FileEntity>** - Dynamic file storage
- **List<String>** - Audit log entries
- **Generic Collections** - Type-safe data structures

## Files Created During Execution

- User uploaded files (e.g., `document.txt`, `report.txt`)
- Files are stored in the current working directory
- Can be read back using option 15

## Requirements

- Java Development Kit (JDK) 8 or higher
- Any Java IDE (VS Code, IntelliJ IDEA, Eclipse) or command line
- Operating System: Windows, Linux, or macOS

## Academic Purpose

This project demonstrates:
- Professional Java project structure
- Industry-standard package organization
- Comprehensive OOP implementation
- Exception handling best practices
- Collections framework usage
- Clean code principles
- Separation of concerns

## Troubleshooting

### Issue: "Class not found" error
**Solution**: Ensure you're running from the correct directory and using the full classpath

### Issue: Compilation errors
**Solution**: Check that all files are in correct package directories

### Issue: Scanner input issues
**Solution**: Clear scanner buffer after each nextInt() with nextLine()

### Issue: Files not saving to disk
**Solution**: Check write permissions in the current directory

## Contact

For questions or issues, refer to the course instructor or TA.

---
**Version**: 1.0.0  
**Date**: February 2025  
**Course**: Java Programming - 4th Semester
