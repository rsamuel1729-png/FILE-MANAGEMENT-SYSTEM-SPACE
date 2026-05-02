package com.securefiles.services;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.io.File;
import com.securefiles.models.FileEntity;
import com.securefiles.exceptions.FileOperationException;

/**
 * Thread-safe FileManager.
 *
 * Multithreading concepts used:
 * 1. ConcurrentHashMap          - thread-safe map for O(1) file lookup by ID
 * 2. ReentrantReadWriteLock     - allows multiple concurrent readers; exclusive writer
 * 3. AtomicInteger / AtomicLong - lock-free counters for stats
 * 4. ExecutorService (cached)   - async background backup on upload
 * 5. Future<?>                  - track async backup task result
 */
public class FileManager {

    // ── thread-safe file store: fileId → FileEntity ──────────────────────────
    private final ConcurrentHashMap<String, FileEntity> fileMap = new ConcurrentHashMap<>();

    // ── ReadWriteLock: many readers OR one writer at a time ───────────────────
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock  = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    // ── lock-free counters ───────────────────────────────────────────────────
    private final AtomicInteger successCount    = new AtomicInteger(0);
    private final AtomicInteger errorCount      = new AtomicInteger(0);
    private final AtomicInteger activeFilesCount= new AtomicInteger(0);
    private final AtomicLong    usedStorage     = new AtomicLong(0);

    // ── background backup pool ───────────────────────────────────────────────
    private final ExecutorService backupExecutor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "FileBackup-Thread");
        t.setDaemon(true);
        return t;
    });

    // ── instance metadata ────────────────────────────────────────────────────
    private final String managerId;
    private final long   maxStorageLimit = 10485760L;
    private final String allowedFileTypes = "txt, doc, pdf, jpg, png";
    private final String rootDirectory    = "/secure/storage";
    private final String operationMode    = "Standard Production Mode";
    private final boolean autoBackupEnabled    = true;
    private final boolean compressionEnabled   = false;
    private volatile String lastOperation      = "System Initialized";
    private final String accessPolicy    = "Role-Based Access Control";
    private final String retentionPolicy = "30 Days Retention";
    private volatile String managerStatus = "Active and Running";

    public FileManager(String managerId) {
        this.managerId = managerId;
    }

    // ── upload ───────────────────────────────────────────────────────────────
    /**
     * Adds a file to the store (write-locked) then schedules an async backup.
     */
    public void uploadFile(FileEntity file) throws FileOperationException {
        if (file == null) {
            errorCount.incrementAndGet();
            throw new FileOperationException("File entity cannot be null");
        }

        writeLock.lock();
        try {
            fileMap.put(file.getFileId(), file);
            activeFilesCount.incrementAndGet();
            usedStorage.addAndGet(file.getFileContent().length());
            successCount.incrementAndGet();
            lastOperation = "File Upload: " + file.getFileName();
        } finally {
            writeLock.unlock();
        }

        // Async disk backup – runs on a background thread, not blocking the caller
        if (autoBackupEnabled) {
            backupExecutor.submit(() -> {
                try {
                    file.writeToFile();
                } catch (FileOperationException e) {
                    System.err.println("[BackupThread] Backup failed for "
                            + file.getFileName() + ": " + e.getMessage());
                }
            });
        }
    }

    // ── get (read-locked) ────────────────────────────────────────────────────
    public FileEntity getFile(String fileId) throws FileOperationException {
        if (fileId == null || fileId.trim().isEmpty()) {
            errorCount.incrementAndGet();
            throw new FileOperationException("File ID cannot be null or empty");
        }

        readLock.lock();
        try {
            FileEntity file = fileMap.get(fileId);
            if (file == null) {
                errorCount.incrementAndGet();
            }
            return file;
        } finally {
            readLock.unlock();
        }
    }

    // ── delete (write-locked) ────────────────────────────────────────────────
    public void deleteFile(String fileId) throws FileOperationException {
        FileEntity file = getFile(fileId);   // read-locked internally
        if (file == null) return;

        writeLock.lock();
        try {
            usedStorage.addAndGet(-file.getFileContent().length());
            fileMap.remove(fileId);
            activeFilesCount.decrementAndGet();
            successCount.incrementAndGet();
            lastOperation = "File Delete: " + file.getFileName();
        } finally {
            writeLock.unlock();
        }

        // Disk deletion on a background thread
        backupExecutor.submit(() -> {
            File diskFile = new File(file.getFileName());
            if (diskFile.exists() && !diskFile.delete()) {
                System.err.println("[BackupThread] Could not delete file from disk: "
                        + file.getFileName());
            }
        });
    }

    // ── list all (read-locked snapshot) ─────────────────────────────────────
    public List<FileEntity> getAllFiles() {
        readLock.lock();
        try {
            return new ArrayList<>(fileMap.values());
        } finally {
            readLock.unlock();
        }
    }

    // ── status display ───────────────────────────────────────────────────────
    public void displayManagerInfo() {
        long used = usedStorage.get();
        System.out.println("\n===== FILE MANAGER STATUS =====");
        System.out.println("Manager ID: " + managerId);
        System.out.println("Active Files Count: " + activeFilesCount.get());
        System.out.println("Max Storage Limit: " + maxStorageLimit + " bytes");
        System.out.println("Used Storage: " + used + " bytes");
        System.out.println("Available Storage: " + (maxStorageLimit - used) + " bytes");
        System.out.println("Storage Usage: " + (maxStorageLimit > 0 ? used * 100 / maxStorageLimit : 0) + "%");
        System.out.println("Allowed File Types: " + allowedFileTypes);
        System.out.println("Root Directory: " + rootDirectory);
        System.out.println("Operation Mode: " + operationMode);
        System.out.println("Auto Backup: " + (autoBackupEnabled ? "Enabled" : "Disabled"));
        System.out.println("Compression: " + (compressionEnabled ? "Enabled" : "Disabled"));
        System.out.println("Last Operation: " + lastOperation);
        System.out.println("Success Count: " + successCount.get());
        System.out.println("Error Count: " + errorCount.get());
        System.out.println("Access Policy: " + accessPolicy);
        System.out.println("Retention Policy: " + retentionPolicy);
        System.out.println("Manager Status: " + managerStatus);
        System.out.println("===============================\n");
    }

    /** Shut down the background backup pool gracefully. */
    public void shutdown() {
        backupExecutor.shutdown();
        try {
            if (!backupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                backupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            backupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
