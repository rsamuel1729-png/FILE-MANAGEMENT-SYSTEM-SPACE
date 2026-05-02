package com.securefiles.main;

import java.util.*;
import java.util.concurrent.*;
import com.securefiles.models.*;
import com.securefiles.services.*;
import com.securefiles.exceptions.*;
import com.securefiles.utils.ThreadPoolManager;

/**
 * Main entry point for the Secure File Management System.
 *
 * Multithreading integrations in this class:
 * 1. ThreadPoolManager.scheduleHeartbeat - periodic session-activity check
 * 2. ThreadPoolManager.submitFileOperation (Future) - async encrypt / decrypt
 * 3. ThreadPoolManager.acquireEncryptionPermit / release - semaphore guard
 * 4. Graceful shutdown via ThreadPoolManager.shutdown() + FileManager.shutdown()
 */
public class SecureFileManagementSystem {

    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        try {
            runApplication();
        } catch (Exception e) {
            System.err.println("Critical system error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (scanner != null) scanner.close();
            ThreadPoolManager.shutdown();
        }
    }

    private static void runApplication() {
        SystemConfig config = new SystemConfig();
        FileManager fileManager = new FileManager("FM-001");
        EncryptionProfile encryptionProfile = new EncryptionProfile("EP-001", "AES-256");
        KeyManager keyManager = new KeyManager("KM-001", "MasterKey", "ADMIN");

        System.out.println("\n========================================");
        System.out.println("   SECURE FILE MANAGEMENT SYSTEM");
        System.out.println("========================================\n");

        config.displaySystemInfo();

        User currentUser = new User("U001", "john", "pass123", "User", "john@example.com");
        Admin admin      = new Admin("A001", "admin", "admin123", "admin@example.com");

        User loggedInUser = null;

        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            if (currentUser.authenticate(username, password)) {
                loggedInUser = currentUser;
            } else if (admin.authenticate(username, password)) {
                loggedInUser = admin;
            }

            if (loggedInUser == null) {
                throw new AuthenticationException("Invalid username or password");
            }
            if (loggedInUser.isLocked()) {
                throw new AuthenticationException("Account is locked! Contact administrator.");
            }

        } catch (AuthenticationException e) {
            System.err.println("\nAuthentication Error: " + e.getMessage());
            return;
        } catch (Exception e) {
            System.err.println("\nUnexpected error during login: " + e.getMessage());
            return;
        }

        System.out.println("\nLogin successful! Welcome " + loggedInUser.getUsername());

        Session session    = new Session("S001", loggedInUser.getUserId());
        AuditLog auditLog  = new AuditLog("AL001", loggedInUser.getUserId());
        auditLog.logAction("LOGIN", "User logged in successfully");

        // ── Heartbeat: periodic session-activity ping every 60 s ─────────────
        // Uses ScheduledExecutorService inside ThreadPoolManager
        ScheduledFuture<?> heartbeat = ThreadPoolManager.scheduleHeartbeat(() -> {
            if (session.isSessionActive()) {
                session.recordActivity();
            }
        }, 60_000, 60_000);

        AccessControl currentAccess = null;

        while (session.isSessionActive()) {
            try {
                displayMenu();
                int choice = getMenuChoice();

                switch (choice) {
                    case 1:
                        handleFileUpload(fileManager, loggedInUser, auditLog);
                        currentAccess = new AccessControl(
                                "AC00" + fileManager.getAllFiles().size(),
                                loggedInUser.getUserId(),
                                "F00"  + fileManager.getAllFiles().size());
                        break;
                    case 2:  handleViewFiles(fileManager, loggedInUser);                               break;
                    case 3:  handleDownloadFile(fileManager, loggedInUser, auditLog);                  break;
                    case 4:  handleDeleteFile(fileManager, loggedInUser, auditLog);                    break;
                    case 5:  handleEncryptFile(fileManager, loggedInUser, encryptionProfile, keyManager, auditLog); break;
                    case 6:  handleDecryptFile(fileManager, loggedInUser, encryptionProfile, auditLog); break;
                    case 7:  handleViewFileMetadata(fileManager, loggedInUser);                        break;
                    case 8:
                        loggedInUser.displayInfo();
                        if (loggedInUser.getRole().equals("Admin")) {
                            ((Admin) loggedInUser).displayAdminInfo();
                        }
                        break;
                    case 9:  session.displaySessionInfo();           break;
                    case 10: fileManager.displayManagerInfo();       break;
                    case 11: encryptionProfile.displayProfileInfo(); break;
                    case 12: keyManager.displayKeyInfo();            break;
                    case 13:
                        if (currentAccess != null) currentAccess.displayAccessInfo();
                        else System.out.println("\nNo access control information available. Please upload a file first.");
                        break;
                    case 14: AuditLog.displayLogs(); break;
                    case 15: handleReadFileFromDisk(fileManager, loggedInUser, auditLog); break;
                    case 16:
                        heartbeat.cancel(false);   // stop heartbeat thread
                        session.endSession();
                        loggedInUser.logout();
                        auditLog.logAction("LOGOUT", "User logged out successfully");
                        fileManager.shutdown();
                        System.out.println("\nLogged out successfully! Thank you for using Secure File Management System.");
                        break;
                    default:
                        System.out.println("\nInvalid choice! Please try again.");
                }
            } catch (InputMismatchException e) {
                System.err.println("\nInvalid input! Please enter a number.");
                scanner.nextLine();
            } catch (Exception e) {
                System.err.println("\nError: " + e.getMessage());
            }
        }
    }

    // ── menu helpers ─────────────────────────────────────────────────────────

    private static void displayMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1.  Upload File");
        System.out.println("2.  View All Files");
        System.out.println("3.  Download File");
        System.out.println("4.  Delete File");
        System.out.println("5.  Encrypt File");
        System.out.println("6.  Decrypt File");
        System.out.println("7.  View File Metadata");
        System.out.println("8.  View User Profile");
        System.out.println("9.  View Session Info");
        System.out.println("10. View File Manager Status");
        System.out.println("11. View Encryption Profile");
        System.out.println("12. View Key Manager Info");
        System.out.println("13. View Access Control Info");
        System.out.println("14. View Audit Logs");
        System.out.println("15. Read File from Disk");
        System.out.println("16. Logout");
        System.out.print("Enter choice: ");
    }

    private static int getMenuChoice() throws InputMismatchException {
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    // ── handlers ─────────────────────────────────────────────────────────────

    private static void handleFileUpload(FileManager fileManager, User user, AuditLog auditLog) {
        try {
            System.out.print("Enter file name (with extension): ");
            String fileName = scanner.nextLine();
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new FileOperationException("File name cannot be empty");
            }

            System.out.print("Enter file content: ");
            String content = scanner.nextLine();
            if (content == null) content = "";

            String fileId = "F00" + (fileManager.getAllFiles().size() + 1);
            FileEntity file = new FileEntity(fileId, fileName, user.getUserId(), content);
            fileManager.uploadFile(file);
            auditLog.logAction("UPLOAD", "File uploaded: " + fileName);
            System.out.println("\nFile uploaded successfully! (Backup running in background)");
        } catch (FileOperationException e) {
            System.err.println("\nFile upload failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nUnexpected error during upload: " + e.getMessage());
        }
    }

    private static void handleViewFiles(FileManager fileManager, User user) {
        try {
            System.out.println("\n========== YOUR FILES ==========");
            List<FileEntity> allFiles = fileManager.getAllFiles();
            boolean hasFiles = false;
            for (FileEntity file : allFiles) {
                if (file.getOwnerId().equals(user.getUserId())) {
                    System.out.println("File ID: " + file.getFileId()
                            + " | Name: "      + file.getFileName()
                            + " | Encrypted: " + (file.getIsEncrypted() ? "Yes" : "No")
                            + " | Size: "      + file.getFileContent().length() + " bytes");
                    hasFiles = true;
                }
            }
            if (!hasFiles) System.out.println("No files found.");
            System.out.println("================================");
        } catch (Exception e) {
            System.err.println("\nError viewing files: " + e.getMessage());
        }
    }

    private static void handleDownloadFile(FileManager fileManager, User user, AuditLog auditLog) {
        try {
            System.out.print("Enter file ID to download: ");
            String fileId = scanner.nextLine();
            FileEntity file = fileManager.getFile(fileId);
            if (file == null) throw new FileOperationException("File not found");
            if (!file.getOwnerId().equals(user.getUserId()))
                throw new AccessDeniedException("Access denied! You don't own this file.");

            System.out.println("\n========== FILE CONTENT ==========");
            System.out.println("File Name: " + file.getFileName());
            System.out.println("Content:\n" + file.getFileContent());
            System.out.println("==================================");
            auditLog.logAction("DOWNLOAD", "File downloaded: " + file.getFileName());
        } catch (FileOperationException | AccessDeniedException e) {
            System.err.println("\n" + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nError downloading file: " + e.getMessage());
        }
    }

    private static void handleDeleteFile(FileManager fileManager, User user, AuditLog auditLog) {
        try {
            System.out.print("Enter file ID to delete: ");
            String fileId = scanner.nextLine();
            FileEntity file = fileManager.getFile(fileId);
            if (file == null) throw new FileOperationException("File not found");
            if (!file.getOwnerId().equals(user.getUserId()))
                throw new AccessDeniedException("Access denied! You don't own this file.");

            String fileName = file.getFileName();
            fileManager.deleteFile(fileId);
            auditLog.logAction("DELETE", "File deleted: " + fileName);
            System.out.println("\nFile deleted successfully!");
        } catch (FileOperationException | AccessDeniedException e) {
            System.err.println("\n" + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nError deleting file: " + e.getMessage());
        }
    }

    /**
     * Encrypt file using a Future for async execution + Semaphore guard.
     * The encryption is submitted to the thread pool; the main thread
     * then waits on future.get() so the menu only proceeds after it completes.
     */
    private static void handleEncryptFile(FileManager fileManager, User user,
                                          EncryptionProfile encryptionProfile,
                                          KeyManager keyManager, AuditLog auditLog) {
        try {
            System.out.print("Enter file ID to encrypt: ");
            String fileId = scanner.nextLine();
            FileEntity file = fileManager.getFile(fileId);
            if (file == null) throw new FileOperationException("File not found");
            if (!file.getOwnerId().equals(user.getUserId()))
                throw new AccessDeniedException("Access denied! You don't own this file.");
            if (file.getIsEncrypted()) { System.out.println("\nFile is already encrypted!"); return; }

            // Submit encrypt job to thread pool; acquire semaphore before crypto
            Future<String> encryptFuture = ThreadPoolManager.submitFileOperation(() -> {
                ThreadPoolManager.acquireEncryptionPermit(); // Semaphore.acquire()
                try {
                    return encryptionProfile.encrypt(file.getFileContent());
                } finally {
                    ThreadPoolManager.releaseEncryptionPermit(); // Semaphore.release()
                }
            });

            // Block on Future to get result (demonstrates Future.get())
            String encrypted = encryptFuture.get(10, TimeUnit.SECONDS);
            file.setFileContent(encrypted);
            file.setIsEncrypted(true);
            keyManager.rotateKey();
            auditLog.logAction("ENCRYPT", "File encrypted: " + file.getFileName());
            System.out.println("\nFile encrypted successfully!");

        } catch (TimeoutException e) {
            System.err.println("\nEncryption timed out. Please try again.");
        } catch (ExecutionException e) {
            System.err.println("\nEncryption failed: " + e.getCause().getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupt status
            System.err.println("\nEncryption interrupted. Please try again.");
        } catch (FileOperationException | AccessDeniedException e) {
            System.err.println("\n" + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nError encrypting file: " + e.getMessage());
        }
    }

    /**
     * Decrypt file – mirrors handleEncryptFile with the same Future + Semaphore pattern.
     */
    private static void handleDecryptFile(FileManager fileManager, User user,
                                          EncryptionProfile encryptionProfile,
                                          AuditLog auditLog) {
        try {
            System.out.print("Enter file ID to decrypt: ");
            String fileId = scanner.nextLine();
            FileEntity file = fileManager.getFile(fileId);
            if (file == null) throw new FileOperationException("File not found");
            if (!file.getOwnerId().equals(user.getUserId()))
                throw new AccessDeniedException("Access denied! You don't own this file.");
            if (!file.getIsEncrypted()) { System.out.println("\nFile is not encrypted!"); return; }

            Future<String> decryptFuture = ThreadPoolManager.submitFileOperation(() -> {
                ThreadPoolManager.acquireEncryptionPermit();
                try {
                    return encryptionProfile.decrypt(file.getFileContent());
                } finally {
                    ThreadPoolManager.releaseEncryptionPermit();
                }
            });

            String decrypted = decryptFuture.get(10, TimeUnit.SECONDS);
            file.setFileContent(decrypted);
            file.setIsEncrypted(false);
            auditLog.logAction("DECRYPT", "File decrypted: " + file.getFileName());
            System.out.println("\nFile decrypted successfully!");

        } catch (TimeoutException e) {
            System.err.println("\nDecryption timed out. Please try again.");
        } catch (ExecutionException e) {
            System.err.println("\nDecryption failed: " + e.getCause().getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupt status
            System.err.println("\nDecryption interrupted. Please try again.");
        } catch (FileOperationException | AccessDeniedException e) {
            System.err.println("\n" + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nError decrypting file: " + e.getMessage());
        }
    }

    private static void handleViewFileMetadata(FileManager fileManager, User user) {
        try {
            System.out.print("Enter file ID to view metadata: ");
            String fileId = scanner.nextLine();
            FileEntity file = fileManager.getFile(fileId);
            if (file == null) throw new FileOperationException("File not found");
            if (!file.getOwnerId().equals(user.getUserId()))
                throw new AccessDeniedException("Access denied! You don't own this file.");
            file.displayFileInfo();
        } catch (FileOperationException | AccessDeniedException e) {
            System.err.println("\n" + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nError viewing metadata: " + e.getMessage());
        }
    }

    private static void handleReadFileFromDisk(FileManager fileManager, User user, AuditLog auditLog) {
        try {
            System.out.print("Enter file name to read from disk: ");
            String fileName = scanner.nextLine();
            boolean found = false;
            for (FileEntity file : fileManager.getAllFiles()) {
                if (file.getFileName().equals(fileName) && file.getOwnerId().equals(user.getUserId())) {
                    file.readFromFile();
                    auditLog.logAction("READ_DISK", "File read from disk: " + fileName);
                    found = true;
                    break;
                }
            }
            if (!found) throw new FileOperationException("File not found in your files list!");
        } catch (FileOperationException e) {
            System.err.println("\n" + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nError reading file from disk: " + e.getMessage());
        }
    }
}
