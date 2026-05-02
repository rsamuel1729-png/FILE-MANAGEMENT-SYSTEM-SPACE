package com.securefiles.services;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe KeyManager.
 *
 * Multithreading concepts used:
 * 1. ReentrantLock  - protects key rotation (mutable state: keyUsageCount, lastAccessed, keyVersion)
 * 2. AtomicInteger  - lock-free usage counter readable from any thread
 * 3. volatile       - lastAccessed visible without full lock on reads
 */
public class KeyManager {

    // ── mutable state ────────────────────────────────────────────────────────
    private final AtomicInteger keyUsageCount = new AtomicInteger(0);
    private volatile String lastAccessed;
    private volatile String keyVersion;

    // ── lock for compound key-rotation operation ──────────────────────────────
    private final ReentrantLock rotationLock = new ReentrantLock();

    // ── immutable metadata ───────────────────────────────────────────────────
    private final String keyId;
    private final String keyName;
    private final String keyOwner;
    private final String keyCreationDate   = "2025-01-01";
    private final String keyExpiryDate     = "2026-01-01";
    private final String keyStatus         = "Active and Valid";
    private final String keyRotationPolicy = "Yearly Automatic Rotation";
    private final String keyAccessLevel    = "High Security Level";
    private final String keyBackupStatus   = "Backed Up Securely";
    private final String keyStorageLocation= "Hardware Security Module";
    private final int    keyLength         = 256;
    private final String remarks           = "Primary encryption key for system";

    public KeyManager(String keyId, String keyName, String keyOwner) {
        this.keyId        = keyId;
        this.keyName      = keyName;
        this.keyOwner     = keyOwner;
        this.lastAccessed = "Never";
        this.keyVersion   = "1.0";
    }

    public void displayKeyInfo() {
        System.out.println("\n===== KEY MANAGER INFO =====");
        System.out.println("Key ID: " + keyId);
        System.out.println("Key Name: " + keyName);
        System.out.println("Key Owner: " + keyOwner);
        System.out.println("Key Creation Date: " + keyCreationDate);
        System.out.println("Key Expiry Date: " + keyExpiryDate);
        System.out.println("Key Status: " + keyStatus);
        System.out.println("Key Usage Count: " + keyUsageCount.get());
        System.out.println("Key Rotation Policy: " + keyRotationPolicy);
        System.out.println("Key Access Level: " + keyAccessLevel);
        System.out.println("Key Backup Status: " + keyBackupStatus);
        System.out.println("Key Storage Location: " + keyStorageLocation);
        System.out.println("Key Length: " + keyLength + " bits");
        System.out.println("Key Version: " + keyVersion);
        System.out.println("Last Accessed: " + lastAccessed);
        System.out.println("Remarks: " + remarks);
        System.out.println("============================\n");
    }

    /**
     * Atomic key-rotation.
     * Lock ensures count + lastAccessed update is not split across threads.
     */
    public void rotateKey() {
        rotationLock.lock();
        try {
            int newCount = keyUsageCount.incrementAndGet();
            lastAccessed = "2025-02-09 " + System.currentTimeMillis() % 86400000 / 3600000 + ":00:00";
            // Bump minor version every 10 uses
            if (newCount % 10 == 0) {
                keyVersion = "1." + (newCount / 10);
            }
        } catch (Exception e) {
            System.err.println("Error rotating key: " + e.getMessage());
        } finally {
            rotationLock.unlock();
        }
    }

    public int getKeyUsageCount() { return keyUsageCount.get(); }
}
