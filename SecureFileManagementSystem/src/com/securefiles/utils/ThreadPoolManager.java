package com.securefiles.utils;

import java.util.concurrent.*;

/**
 * Application-wide thread-pool manager.
 *
 * Multithreading concepts demonstrated:
 * 1. ScheduledExecutorService  - runs periodic tasks (session heartbeat)
 * 2. ExecutorService (fixed)   - bounded pool for file operations
 * 3. Future<T>                 - represents the result of an async computation
 * 4. Callable<T>               - task that returns a value (vs Runnable)
 * 5. CountDownLatch            - synchronisation barrier (wait for N tasks)
 * 6. Semaphore                 - limits concurrent access to a resource
 */
public class ThreadPoolManager {

    // ── fixed pool for concurrent file operations ─────────────────────────────
    private static final ExecutorService fileOperationPool =
            Executors.newFixedThreadPool(4, r -> {
                Thread t = new Thread(r, "FileOp-Thread");
                t.setDaemon(true);
                return t;
            });

    // ── scheduled pool for periodic background tasks ──────────────────────────
    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "Scheduler-Thread");
                t.setDaemon(true);
                return t;
            });

    // ── semaphore: at most 2 concurrent encrypt / decrypt operations ──────────
    private static final Semaphore encryptionSemaphore = new Semaphore(2, true);

    private ThreadPoolManager() { /* utility class */ }

    /**
     * Submit a Callable to the file-operation pool and return its Future.
     * Caller can call future.get() to block until the result is ready.
     */
    public static <T> Future<T> submitFileOperation(Callable<T> task) {
        return fileOperationPool.submit(task);
    }

    /** Submit a Runnable that has no return value. */
    public static Future<?> submitTask(Runnable task) {
        return fileOperationPool.submit(task);
    }

    /**
     * Run tasks in parallel and wait for ALL of them to complete.
     * Uses CountDownLatch as a join barrier.
     */
    public static void runParallel(Runnable... tasks) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(tasks.length);
        for (Runnable task : tasks) {
            fileOperationPool.submit(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(); // Block until all tasks finish
    }

    /**
     * Acquire the encryption semaphore before doing encrypt/decrypt.
     * Limits concurrent crypto operations to 2, preventing CPU saturation.
     */
    public static void acquireEncryptionPermit() throws InterruptedException {
        encryptionSemaphore.acquire();
    }

    /** Release the encryption semaphore after the operation completes. */
    public static void releaseEncryptionPermit() {
        encryptionSemaphore.release();
    }

    /**
     * Schedule a periodic heartbeat task (e.g., session timeout checks).
     * @param task     the job to run
     * @param delayMs  initial delay in milliseconds
     * @param periodMs period in milliseconds
     */
    public static ScheduledFuture<?> scheduleHeartbeat(Runnable task,
                                                        long delayMs,
                                                        long periodMs) {
        return scheduler.scheduleAtFixedRate(task, delayMs, periodMs,
                TimeUnit.MILLISECONDS);
    }

    /** Gracefully shut down both pools (call on application exit). */
    public static void shutdown() {
        fileOperationPool.shutdown();
        scheduler.shutdown();
        try {
            if (!fileOperationPool.awaitTermination(5, TimeUnit.SECONDS)) {
                fileOperationPool.shutdownNow();
            }
            if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            fileOperationPool.shutdownNow();
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
