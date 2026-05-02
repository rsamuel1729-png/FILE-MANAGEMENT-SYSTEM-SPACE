package com.securefiles.models;

public class Admin extends User {
    private String adminId;
    private String adminLevel;
    private String permissions;
    private int managedUsersCount;
    private String systemPrivileges;
    private boolean auditAccessEnabled;
    private String lastSystemCheck;
    private boolean emergencyAccess;
    private String adminDepartment;
    private String adminContact;
    private String adminStatus;
    private String assignedModules;
    private int approvalLimit;
    private String escalationLevel;
    private String adminNotes;
    
    public Admin(String adminId, String username, String password, String email) {
        super(adminId, username, password, "Admin", email);
        this.adminId = adminId;
        this.adminLevel = "Level 1 Administrator";
        this.permissions = "Full System Access";
        this.systemPrivileges = "All Privileges Granted";
        this.auditAccessEnabled = true;
        this.emergencyAccess = true;
        this.adminStatus = "Active Administrator";
        this.approvalLimit = 1000000;
        this.managedUsersCount = 50;
        this.lastSystemCheck = "2025-02-09 09:00:00";
        this.adminDepartment = "IT Security Department";
        this.adminContact = "admin@securefiles.com";
        this.assignedModules = "User Management, File Management, Audit Logs";
        this.escalationLevel = "Level 3 Escalation";
        this.adminNotes = "Primary system administrator with full control";
    }
    
    public void displayAdminInfo() {
        System.out.println("\n===== ADMIN PROFILE =====");
        System.out.println("Admin ID: " + adminId);
        System.out.println("Admin Level: " + adminLevel);
        System.out.println("Permissions: " + permissions);
        System.out.println("Managed Users Count: " + managedUsersCount);
        System.out.println("System Privileges: " + systemPrivileges);
        System.out.println("Audit Access Enabled: " + auditAccessEnabled);
        System.out.println("Last System Check: " + lastSystemCheck);
        System.out.println("Emergency Access: " + emergencyAccess);
        System.out.println("Admin Department: " + adminDepartment);
        System.out.println("Admin Contact: " + adminContact);
        System.out.println("Admin Status: " + adminStatus);
        System.out.println("Assigned Modules: " + assignedModules);
        System.out.println("Approval Limit: " + approvalLimit);
        System.out.println("Escalation Level: " + escalationLevel);
        System.out.println("Admin Notes: " + adminNotes);
        System.out.println("=========================\n");
    }
    
    public void manageUsers() {
        try {
            System.out.println("Admin managing users with " + permissions);
        } catch (Exception e) {
            System.err.println("Error in user management: " + e.getMessage());
        }
    }
}
