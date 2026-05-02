package com.securefiles.services;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import com.securefiles.interfaces.Auditable;

/**
 * Thread-safe AuditLog service.
 * 
 * Multithreading concepts used:
 * 1. CopyOnWriteArrayList       - thread-safe list for reading audit logs from multiple threads
 * 2. LinkedBlockingQueue        - producer-consumer queue for async log writes
 * 3. ExecutorService (single thread) - background daemon thread that drains the queue
 * 4. AtomicInteger              - lock-free counter for total log entries
 * 5. volatile                   - ensures visibility of the "running" flag across threads
 */
public class AuditLog implements Auditable {

    // ── thread-safe storage ──────────────────────────────────────────────────
    private static final CopyOnWriteArrayList<String> logs = new CopyOnWriteArrayList<>();

    // ── producer-consumer queue for asynchronous audit writing ───────────────
    private static final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>(1000);

    // ── atomic counter: safe increment without synchronization ───────────────
    private static final AtomicInteger totalLogCount = new AtomicInteger(0);

    // ── background thread executor ───────────────────────────────────────────
    private static final ExecutorService logWriter;

    // ── visibility flag for background thread shutdown ────────────────────────
    private static volatile boolean running = true;

    // ── instance fields ──────────────────────────────────────────────────────
    private String logId;
    private String userId;
    private String actionType;
    private String actionDescription;
    private String timestamp;
    private String affectedResource;
    private String severityLevel;
    private String ipAddress;
    private String deviceInfo;
    private String operationResult;
    private String failureReason;
    private String auditCategory;
    private String loggedBy;
    private String complianceTag;
    private String remarks;

    // ── static initialiser: start the background log-writer thread ────────────
    static {
        logWriter = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "AuditLog-Writer");
            t.setDaemon(true);   // won't block JVM shutdown
            return t;
        });

        logWriter.submit(() -> {
            while (running || !logQueue.isEmpty()) {
                try {
                    // Block for up to 500 ms waiting for a new log entry
                    String entry = logQueue.poll(500, TimeUnit.MILLISECONDS);
                    if (entry != null) {
                        logs.add(entry);
                        totalLogCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        // Register a JVM shutdown hook to flush remaining queue entries
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            logWriter.shutdown();
            try {
                logWriter.awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }, "AuditLog-Shutdown"));
    }

    // ── constructor ──────────────────────────────────────────────────────────
    public AuditLog(String logId, String userId) {
        this.logId = logId;
        this.userId = userId;
        this.timestamp = "2025-02-09 10:00:00";
        this.severityLevel = "INFO";
        this.ipAddress = "192.168.1.100";
        this.operationResult = "Success";
        this.auditCategory = "General Activity";
        this.deviceInfo = "Desktop Console";
        this.loggedBy = "System Audit Service";
        this.complianceTag = "ISO27001 Compliant";
        this.failureReason = "None";
        this.remarks = "Audit log entry created";
    }

    /**
     * Non-blocking log submission.
     * The actual list-add happens on the background writer thread.
     */
    @Override
    public void logAction(String action, String description) {
        try {
            if (action == null || description == null) {
                throw new IllegalArgumentException("Action and description cannot be null");
            }

            this.actionType = action;
            this.actionDescription = description;
            this.affectedResource = description;
            this.timestamp = "2025-02-09 " + System.currentTimeMillis() % 86400000 / 3600000 + ":00:00";

            String logEntry = "[" + timestamp + "] [" + severityLevel + "] User:" + userId
                    + " | Action:" + action
                    + " | " + description
                    + " | Result:" + operationResult;

            // Offer to queue (non-blocking); fall back to direct add if queue is full
            if (!logQueue.offer(logEntry)) {
                logs.add(logEntry);
                totalLogCount.incrementAndGet();
            }
        } catch (Exception e) {
            System.err.println("Error logging action: " + e.getMessage());
        }
    }

    /** Thread-safe read of all logs (CopyOnWriteArrayList snapshot). */
    public static void displayLogs() {
        try {
            System.out.println("\n========== COMPLETE AUDIT TRAIL ==========");
            List<String> snapshot = new ArrayList<>(logs); // safe read
            if (snapshot.isEmpty()) {
                System.out.println("No audit logs available.");
            } else {
                for (String log : snapshot) {
                    System.out.println(log);
                }
                System.out.println("Total Log Entries: " + snapshot.size());
            }
            System.out.println("==========================================\n");
        } catch (Exception e) {
            System.err.println("Error displaying logs: " + e.getMessage());
        }
    }

    /** Returns the atomic count of logs committed to the list so far. */
    public static int getTotalLogCount() {
        return totalLogCount.get();
    }
}
