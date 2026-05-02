package com.securefiles.models;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import com.securefiles.interfaces.Authenticatable;
import com.securefiles.exceptions.AuthenticationException;

/**
 * Thread-safe User model.
 *
 * Multithreading concepts used:
 * 1. AtomicInteger  - lock-free failed-login counter (thread-safe increment & compare)
 * 2. ReentrantLock  - guards the compound check-and-lock logic during authentication
 * 3. volatile       - lastLoginTime / lastLogoutTime / accountStatus visible across threads
 */
public class User extends Person implements Authenticatable {

    // ── lock-free counter ────────────────────────────────────────────────────
    private final AtomicInteger failedLoginAttempts = new AtomicInteger(0);

    // ── lock for compound authenticate operation ──────────────────────────────
    private final ReentrantLock authLock = new ReentrantLock();

    // ── volatile mutable fields ───────────────────────────────────────────────
    private volatile String  accountStatus   = "Active";
    private volatile boolean isAccountLocked = false;
    private volatile String  lastLoginTime   = "Never";
    private volatile String  lastLogoutTime  = "Never";

    // ── immutable / stable fields ─────────────────────────────────────────────
    private final String userId;
    private final String username;
    private final String password;
    private final String role;
    private final String email;
    private final String phoneNumber  = "9876543210";
    private final String createdDate  = "2025-01-31";
    private final String updatedDate  = "2025-01-31";
    private final String securityQuestion = "What is your favorite color?";
    private final String securityAnswer   = "Blue";

    public User(String userId, String username, String password, String role, String email) {
        this.userId   = userId;
        this.username = username;
        this.password = password;
        this.role     = role;
        this.email    = email;

        // Person fields
        this.personId   = userId;
        this.name       = username;
        this.status     = "Active";
        this.contact    = "9876543210";
        this.address    = "123 Main Street";
        this.nationality= "Indian";
        this.gender     = "Male";
        this.age        = 25;
        this.qualification = "BTech CSE";
        this.department = "IT Department";
        this.designation= "User";
        this.joinDate   = "2025-01-01";
        this.remarks    = "Regular user account";
        this.emergencyContact = "9988776655";
        this.bloodGroup = "O+";
    }

    /**
     * Thread-safe authentication.
     * Lock protects the read-increment-compare-lock sequence so two concurrent
     * login attempts cannot both succeed or both reach the lock threshold.
     */
    @Override
    public boolean authenticate(String username, String password) {
        authLock.lock();
        try {
            if (username == null || password == null) {
                throw new AuthenticationException("Username and password cannot be null");
            }

            if (this.username.equals(username) && this.password.equals(password)) {
                lastLoginTime = getCurrentTimestamp();
                failedLoginAttempts.set(0);
                return true;
            }

            int attempts = failedLoginAttempts.incrementAndGet();
            if (attempts >= 3) {
                isAccountLocked = true;
                accountStatus   = "Locked";
            }
            return false;

        } catch (AuthenticationException e) {
            System.err.println("Authentication Error: " + e.getMessage());
            return false;
        } finally {
            authLock.unlock();
        }
    }

    @Override
    public void logout() {
        lastLogoutTime = getCurrentTimestamp();
    }

    public void displayInfo() {
        System.out.println("\n===== USER PROFILE =====");
        System.out.println("User ID: " + userId);
        System.out.println("Username: " + username);
        System.out.println("Role: " + role);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Account Status: " + accountStatus);
        System.out.println("Failed Login Attempts: " + failedLoginAttempts.get());
        System.out.println("Account Locked: " + (isAccountLocked ? "Yes" : "No"));
        System.out.println("Last Login: " + lastLoginTime);
        System.out.println("Last Logout: " + lastLogoutTime);
        System.out.println("Address: " + address);
        System.out.println("Nationality: " + nationality);
        System.out.println("Gender: " + gender);
        System.out.println("Age: " + age);
        System.out.println("Qualification: " + qualification);
        System.out.println("Department: " + department);
        System.out.println("Designation: " + designation);
        System.out.println("Join Date: " + joinDate);
        System.out.println("Blood Group: " + bloodGroup);
        System.out.println("Emergency Contact: " + emergencyContact);
        System.out.println("Security Question: " + securityQuestion);
        System.out.println("Created Date: " + createdDate);
        System.out.println("Updated Date: " + updatedDate);
        System.out.println("Remarks: " + remarks);
        System.out.println("========================\n");
    }

    private String getCurrentTimestamp() {
        return "2025-02-09 " + System.currentTimeMillis() % 86400000 / 3600000 + ":00:00";
    }

    public String getUserId()   { return userId;         }
    public String getUsername() { return username;       }
    public String getRole()     { return role;           }
    public String getEmail()    { return email;          }
    public boolean isLocked()   { return isAccountLocked;}
}
