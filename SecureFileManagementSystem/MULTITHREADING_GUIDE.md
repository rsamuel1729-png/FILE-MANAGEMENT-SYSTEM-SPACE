# Multithreading Additions — Secure File Management System

## Summary of Changes

Seven Java files were modified or created to add thread safety throughout the system.
One new utility class (`ThreadPoolManager`) was added.

---

## Concepts Used & Where

| Concept | Class | Purpose |
|---|---|---|
| `ConcurrentHashMap` | `FileManager` | Lock-free O(1) file lookup by ID |
| `ReentrantReadWriteLock` | `FileManager` | Multiple readers OR one writer at a time |
| `AtomicInteger` / `AtomicLong` | `FileManager`, `AuditLog`, `User`, `KeyManager`, `Session` | Lock-free counters |
| `ExecutorService` (cached pool) | `FileManager` | Async background disk backup on upload/delete |
| `ExecutorService` (single thread) | `AuditLog` | Background daemon thread draining the log queue |
| `LinkedBlockingQueue` | `AuditLog` | Producer-consumer queue for non-blocking log writes |
| `CopyOnWriteArrayList` | `AuditLog` | Thread-safe list safe for concurrent iteration |
| `ReentrantLock` | `EncryptionProfile`, `KeyManager`, `FileEntity`, `User` | Mutual exclusion on compound operations |
| `volatile` | `FileManager`, `FileEntity`, `Session`, `AuditLog`, `User` | Visibility of mutable fields across threads |
| `AtomicBoolean` | `Session` | Lock-free session flag with compare-and-set |
| `ExecutorService` (fixed pool) | `ThreadPoolManager` | Bounded pool for file operations |
| `ScheduledExecutorService` | `ThreadPoolManager` | Periodic session heartbeat task |
| `Future<T>` / `Callable<T>` | `ThreadPoolManager`, `Main` | Async encrypt/decrypt with result retrieval |
| `CountDownLatch` | `ThreadPoolManager` | Barrier to wait for N parallel tasks |
| `Semaphore` | `ThreadPoolManager` | Limit concurrent crypto operations to 2 |
| `shutdown hook` | `AuditLog` | Flush remaining log queue on JVM exit |
| `synchronized` (method) | `FileEntity` | Prevent concurrent disk I/O on same file |

---

## File-by-File Changes

### `services/FileManager.java` (modified)
- `ArrayList` → `ConcurrentHashMap` for file storage
- `ReentrantReadWriteLock` wraps upload, get, delete, getAllFiles
- `AtomicInteger` / `AtomicLong` replace plain int/long counters
- `ExecutorService` (cached thread pool) runs disk backup asynchronously

### `services/AuditLog.java` (modified)
- `ArrayList` → `CopyOnWriteArrayList` for the shared log list
- `LinkedBlockingQueue` buffers incoming log entries (producer side)
- Single background daemon thread (consumer) drains the queue
- `AtomicInteger` counts committed entries
- JVM `ShutdownHook` flushes remaining queue entries on exit

### `services/EncryptionProfile.java` (modified)
- `ReentrantLock` serialises encrypt/decrypt (protects `lastUsed`, `usageCount`)
- `AtomicInteger` tracks usage count lock-free
- `volatile String lastUsed` for cross-thread visibility

### `services/KeyManager.java` (modified)
- `ReentrantLock` makes `rotateKey()` atomic (count + lastAccessed + version)
- `AtomicInteger` for `keyUsageCount`
- `volatile` for `lastAccessed` and `keyVersion`

### `models/FileEntity.java` (modified)
- `ReentrantLock` protects `setFileContent()`, `setIsEncrypted()`, `getFileContent()`, `displayFileInfo()`
- `synchronized writeToFile()` / `readFromFile()` prevent concurrent disk I/O
- `volatile` on `fileStatus`, `lastModifiedDate`, `backupStatus`

### `models/User.java` (modified)
- `ReentrantLock` makes `authenticate()` compound check-and-lock atomic
- `AtomicInteger` for `failedLoginAttempts`
- `volatile` on `accountStatus`, `isAccountLocked`, `lastLoginTime`, `lastLogoutTime`

### `models/Session.java` (modified)
- `AtomicBoolean` for `isActive` with `compareAndSet` in `endSession()` (idempotent)
- `AtomicInteger` for `failedActivityCount`
- `volatile` on `sessionStatus`, `logoutTime`, `lastActivityTime`

### `utils/ThreadPoolManager.java` (NEW)
- Fixed thread pool (4 threads) for file operations
- `ScheduledExecutorService` for heartbeat tasks
- `Semaphore(2)` limits concurrent crypto to 2 threads
- Generic `submitFileOperation(Callable<T>)` returns `Future<T>`
- `runParallel(Runnable...)` uses `CountDownLatch` as join barrier
- Graceful `shutdown()` with timed `awaitTermination`

### `main/SecureFileManagementSystem.java` (modified)
- `handleEncryptFile` and `handleDecryptFile` submit work via `Future<String>`
- `Semaphore.acquire/release` guards every crypto operation
- `ScheduledFuture` heartbeat updates session every 60 seconds
- `heartbeat.cancel()` and `fileManager.shutdown()` on logout

---

## How to Build & Run

```
# Windows
compile.bat
run.bat

# Linux / macOS
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

Default credentials: `john` / `pass123` (user) or `admin` / `admin123` (admin)
