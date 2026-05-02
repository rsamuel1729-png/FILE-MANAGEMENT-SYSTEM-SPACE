package com.securefiles.models;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe Session model.
 *
 * Multithreading concepts used:
 * 1. AtomicBoolean  - lock-free, thread-safe isActive flag
 *                     (compare-and-set prevents duplicate endSession calls)
 * 2. AtomicInteger  - thread-safe failed-activity counter
 * 3. volatile       - sessionStatus / logoutTime / lastActivityTime are
 *                     updated by one thread and read by another
 */
public class Session {

    // ── atomic session flag ──────────────────────────────────────────────────
    private final AtomicBoolean isActive = new AtomicBoolean(true);

    // ── atomic counter ───────────────────────────────────────────────────────
    private final AtomicInteger failedActivityCount = new AtomicInteger(0);

    // ── volatile mutable state ────────────────────────────────────────────────
    private volatile String sessionStatus    = "Active Session";
    private volatile String logoutTime       = "Not logged out";
    private volatile String lastActivityTime = "2025-02-09 10:00:00";
    private volatile String remarks          = "User session created successfully";

    // ── immutable fields ──────────────────────────────────────────────────────
    private final String sessionId;
    private final String userId;
    private final String loginTime           = "2025-02-09 10:00:00";
    private final String ipAddress           = "192.168.1.100";
    private final String deviceType          = "Desktop Computer";
    private final String browserInfo         = "Console Application";
    private final String authenticationMode  = "Password Based Authentication";
    private final int    sessionTimeout      = 3600;
    private final String sessionToken;

    public Session(String sessionId, String userId) {
        this.sessionId    = sessionId;
        this.userId       = userId;
        this.sessionToken = "TOKEN-" + sessionId + "-" + System.currentTimeMillis();
    }

    public void displaySessionInfo() {
        System.out.println("\n===== SESSION INFORMATION =====");
        System.out.println("Session ID: "          + sessionId);
        System.out.println("User ID: "             + userId);
        System.out.println("Login Time: "          + loginTime);
        System.out.println("Logout Time: "         + logoutTime);
        System.out.println("Session Status: "      + sessionStatus);
        System.out.println("IP Address: "          + ipAddress);
        System.out.println("Device Type: "         + deviceType);
        System.out.println("Browser Info: "        + browserInfo);
        System.out.println("Authentication Mode: " + authenticationMode);
        System.out.println("Session Timeout: "     + sessionTimeout + " seconds");
        System.out.println("Failed Activity Count: "+ failedActivityCount.get());
        System.out.println("Last Activity Time: "  + lastActivityTime);
        System.out.println("Session Token: "       + sessionToken);
        System.out.println("Is Active: "           + (isActive.get() ? "Yes" : "No"));
        System.out.println("Remarks: "             + remarks);
        System.out.println("===============================\n");
    }

    /**
     * End the session atomically.
     * compareAndSet ensures endSession is idempotent even if called from
     * multiple threads simultaneously.
     */
    public void endSession() {
        if (isActive.compareAndSet(true, false)) {
            sessionStatus = "Session Terminated";
            logoutTime    = "2025-02-09 "
                    + System.currentTimeMillis() % 86400000 / 3600000 + ":00:00";
            remarks       = "User logged out successfully";
        }
    }

    /** Thread-safe active check. */
    public boolean isSessionActive() {
        return isActive.get();
    }

    /** Thread-safe activity recording. */
    public void recordActivity() {
        lastActivityTime = "2025-02-09 "
                + System.currentTimeMillis() % 86400000 / 3600000 + ":00:00";
    }

    public void incrementFailedActivity() {
        failedActivityCount.incrementAndGet();
    }
}
