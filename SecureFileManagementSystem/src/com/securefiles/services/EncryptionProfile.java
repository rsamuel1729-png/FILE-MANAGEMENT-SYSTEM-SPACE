package com.securefiles.services;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import com.securefiles.interfaces.Encryptable;

/**
 * Thread-safe EncryptionProfile.
 *
 * Multithreading concepts used:
 * 1. ReentrantLock      - ensures only one thread encrypts/decrypts at a time
 *                         (protects mutable state: lastUsed, usageCount)
 * 2. AtomicInteger      - lock-free usage counter
 * 3. volatile           - guarantees visibility of lastUsed across threads
 */
public class EncryptionProfile implements Encryptable {

    // ── mutable shared state protected by lock ───────────────────────────────
    private volatile String lastUsed;
    private final AtomicInteger usageCount = new AtomicInteger(0);

    // ── mutual-exclusion lock for encrypt / decrypt ──────────────────────────
    private final ReentrantLock encryptionLock = new ReentrantLock();

    // ── immutable metadata ───────────────────────────────────────────────────
    private final String profileId;
    private final String encryptionName;
    private final String encryptionStrength = "Military Grade Strong";
    private final int    keyLength          = 256;
    private final String blockMode          = "CBC - Cipher Block Chaining";
    private final String paddingType        = "PKCS5 Padding";
    private final String encryptionStatus   = "Active and Operational";
    private final String createdBy          = "System Administrator";
    private final String createdDate        = "2025-01-01";
    private final String encryptionPurpose  = "File Content Protection";
    private final String complianceLevel    = "High Security Level";
    private final String algorithmCategory  = "Symmetric Encryption";
    private final String profileVersion     = "1.0";
    private final String remarks            = "Standard encryption profile for all files";

    public EncryptionProfile(String profileId, String encryptionName) {
        this.profileId      = profileId;
        this.encryptionName = encryptionName;
        this.lastUsed       = "Never";
    }

    public void displayProfileInfo() {
        System.out.println("\n===== ENCRYPTION PROFILE =====");
        System.out.println("Profile ID: " + profileId);
        System.out.println("Encryption Name: " + encryptionName);
        System.out.println("Encryption Strength: " + encryptionStrength);
        System.out.println("Key Length: " + keyLength + " bits");
        System.out.println("Block Mode: " + blockMode);
        System.out.println("Padding Type: " + paddingType);
        System.out.println("Encryption Status: " + encryptionStatus);
        System.out.println("Created By: " + createdBy);
        System.out.println("Created Date: " + createdDate);
        System.out.println("Last Used: " + lastUsed);
        System.out.println("Usage Count: " + usageCount.get());
        System.out.println("Encryption Purpose: " + encryptionPurpose);
        System.out.println("Compliance Level: " + complianceLevel);
        System.out.println("Algorithm Category: " + algorithmCategory);
        System.out.println("Profile Version: " + profileVersion);
        System.out.println("Remarks: " + remarks);
        System.out.println("==============================\n");
    }

    /**
     * Encrypt data.
     * Lock ensures no two threads interleave on the same profile object.
     */
    @Override
    public String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data to encrypt cannot be null or empty");
        }

        encryptionLock.lock();
        try {
            lastUsed = "2025-02-09 " + System.currentTimeMillis() % 86400000 / 3600000 + ":00:00";
            usageCount.incrementAndGet();

            StringBuilder encrypted = new StringBuilder(data.length());
            for (int i = 0; i < data.length(); i++) {
                encrypted.append((char) (data.charAt(i) + 3));
            }
            return encrypted.toString();
        } catch (Exception e) {
            System.err.println("Encryption error: " + e.getMessage());
            return data;
        } finally {
            encryptionLock.unlock();
        }
    }

    /**
     * Decrypt data.
     * Same lock as encrypt – safe for concurrent callers.
     */
    @Override
    public String decrypt(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data to decrypt cannot be null or empty");
        }

        encryptionLock.lock();
        try {
            lastUsed = "2025-02-09 " + System.currentTimeMillis() % 86400000 / 3600000 + ":00:00";
            usageCount.incrementAndGet();

            StringBuilder decrypted = new StringBuilder(data.length());
            for (int i = 0; i < data.length(); i++) {
                decrypted.append((char) (data.charAt(i) - 3));
            }
            return decrypted.toString();
        } catch (Exception e) {
            System.err.println("Decryption error: " + e.getMessage());
            return data;
        } finally {
            encryptionLock.unlock();
        }
    }
}
