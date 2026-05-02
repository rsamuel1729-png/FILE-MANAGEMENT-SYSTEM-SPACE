package com.securefiles.models;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import com.securefiles.exceptions.FileOperationException;

/**
 * Thread-safe FileEntity.
 *
 * Multithreading concepts used:
 * 1. ReentrantLock   - protects mutable fields (fileContent, isEncrypted, etc.)
 *                      so concurrent encrypt + read cannot interleave
 * 2. volatile        - fileStatus and lastModifiedDate visible to all threads
 *                      without full lock on every read
 * 3. synchronized writeToFile / readFromFile - prevent concurrent I/O on the
 *    same file path
 */
public class FileEntity {

    // ── lock for compound field updates ──────────────────────────────────────
    private final ReentrantLock lock = new ReentrantLock();

    // ── mutable fields protected by lock ─────────────────────────────────────
    private String fileContent;
    private boolean isEncrypted;
    private String encryptionType;
    private String checksum;
    private long   fileSize;
    private volatile String lastModifiedDate;
    private volatile String backupStatus;
    private volatile String fileLocation;
    private volatile String fileStatus;

    // ── immutable / stable fields ─────────────────────────────────────────────
    private final String fileId;
    private final String fileName;
    private final String ownerId;
    private final String filePath;
    private final String fileType;
    private final String creationDate   = "2025-02-09 10:00:00";
    private final String accessLevel    = "Private - Owner Only";
    private final String version        = "1.0";
    private final String fileCategory   = "Document";
    private final String filePriority   = "Normal";
    private final String remarks        = "User uploaded file";

    public FileEntity(String fileId, String fileName, String ownerId, String content) {
        this.fileId       = fileId;
        this.fileName     = fileName;
        this.ownerId      = ownerId;
        this.fileContent  = content;
        this.filePath     = "/secure/storage/" + fileName;
        this.fileSize     = content.length();
        this.fileType     = getFileExtension(fileName);
        this.isEncrypted  = false;
        this.encryptionType    = "None";
        this.fileStatus        = "Active";
        this.checksum          = "MD5-" + Math.abs(content.hashCode());
        this.fileLocation      = "Primary Storage";
        this.backupStatus      = "Not Backed Up";
        this.lastModifiedDate  = "2025-02-09 10:00:00";
    }

    private String getFileExtension(String name) {
        try {
            int dot = name.lastIndexOf('.');
            return (dot > 0) ? name.substring(dot + 1).toUpperCase() : "TXT";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    // ── thread-safe setters ──────────────────────────────────────────────────

    public void setFileContent(String content) {
        lock.lock();
        try {
            this.fileContent      = content;
            this.fileSize         = content.length();
            this.checksum         = "MD5-" + Math.abs(content.hashCode());
            this.lastModifiedDate = "2025-02-09 "
                    + System.currentTimeMillis() % 86400000 / 3600000 + ":00:00";
        } finally {
            lock.unlock();
        }
    }

    public void setIsEncrypted(boolean encrypted) {
        lock.lock();
        try {
            this.isEncrypted      = encrypted;
            this.encryptionType   = encrypted ? "AES-256" : "None";
            this.lastModifiedDate = "2025-02-09 "
                    + System.currentTimeMillis() % 86400000 / 3600000 + ":00:00";
        } finally {
            lock.unlock();
        }
    }

    // ── thread-safe getters ──────────────────────────────────────────────────

    public String getFileContent() {
        lock.lock();
        try { return fileContent; } finally { lock.unlock(); }
    }

    public boolean getIsEncrypted() {
        lock.lock();
        try { return isEncrypted; } finally { lock.unlock(); }
    }

    public String getFileId()   { return fileId;   }
    public String getFileName() { return fileName; }
    public String getOwnerId()  { return ownerId;  }

    // ── synchronized disk I/O ────────────────────────────────────────────────

    /** Write file content to disk. Synchronized to prevent concurrent writes. */
    public synchronized void writeToFile() throws FileOperationException {
        FileWriter writer = null;
        try {
            File file = new File(fileName);
            writer = new FileWriter(file);
            lock.lock();
            String snapshot;
            try { snapshot = fileContent; } finally { lock.unlock(); }

            writer.write(snapshot);
            this.backupStatus = "Backed Up to Disk";
            this.fileLocation = "Disk: " + file.getAbsolutePath();
            System.out.println("[BackupThread] File written to disk: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new FileOperationException("Error writing file to disk: " + fileName, e);
        } finally {
            try { if (writer != null) writer.close(); }
            catch (IOException e) {
                System.err.println("Error closing file writer: " + e.getMessage());
            }
        }
    }

    /** Read file content from disk. Synchronized to prevent concurrent reads during write. */
    public synchronized void readFromFile() throws FileOperationException {
        Scanner reader = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                throw new FileOperationException("File does not exist: " + fileName);
            }
            reader = new Scanner(file);
            StringBuilder content = new StringBuilder();
            while (reader.hasNextLine()) {
                content.append(reader.nextLine()).append("\n");
            }
            System.out.println("\n===== FILE CONTENT =====");
            System.out.println(content.toString());
            System.out.println("========================\n");
        } catch (FileNotFoundException e) {
            throw new FileOperationException("File not found: " + fileName, e);
        } finally {
            if (reader != null) reader.close();
        }
    }

    // ── display ──────────────────────────────────────────────────────────────
    public void displayFileInfo() {
        lock.lock();
        try {
            System.out.println("\n===== FILE METADATA =====");
            System.out.println("File ID: " + fileId);
            System.out.println("File Name: " + fileName);
            System.out.println("File Path: " + filePath);
            System.out.println("File Size: " + fileSize + " bytes");
            System.out.println("File Type: " + fileType);
            System.out.println("Owner ID: " + ownerId);
            System.out.println("Creation Date: " + creationDate);
            System.out.println("Last Modified: " + lastModifiedDate);
            System.out.println("Access Level: " + accessLevel);
            System.out.println("Encrypted: " + (isEncrypted ? "Yes" : "No"));
            System.out.println("Encryption Type: " + encryptionType);
            System.out.println("Checksum: " + checksum);
            System.out.println("Version: " + version);
            System.out.println("File Status: " + fileStatus);
            System.out.println("Category: " + fileCategory);
            System.out.println("Priority: " + filePriority);
            System.out.println("Storage Location: " + fileLocation);
            System.out.println("Backup Status: " + backupStatus);
            System.out.println("Remarks: " + remarks);
            System.out.println("=========================\n");
        } finally {
            lock.unlock();
        }
    }
}
