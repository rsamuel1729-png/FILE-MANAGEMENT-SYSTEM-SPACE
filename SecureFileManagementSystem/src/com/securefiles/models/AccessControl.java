package com.securefiles.models;

public class AccessControl {
    private String accessId;
    private String userId;
    private String fileId;
    private boolean permissionRead;
    private boolean permissionWrite;
    private boolean permissionDelete;
    private boolean permissionShare;
    private String grantedBy;
    private String grantedDate;
    private String expiryDate;
    private String accessStatus;
    private String accessLevel;
    private boolean overrideAllowed;
    private String policyName;
    private String remarks;
    
    public AccessControl(String accessId, String userId, String fileId) {
        this.accessId = accessId;
        this.userId = userId;
        this.fileId = fileId;
        this.permissionRead = true;
        this.permissionWrite = true;
        this.permissionDelete = true;
        this.permissionShare = false;
        this.grantedDate = "2025-02-09";
        this.expiryDate = "2026-02-09";
        this.accessStatus = "Active Access Granted";
        this.accessLevel = "Full Control Access";
        this.overrideAllowed = false;
        this.policyName = "Standard Owner Policy";
        this.grantedBy = "System";
        this.remarks = "Owner has full access to file";
    }
    
    public void displayAccessInfo() {
        System.out.println("\n===== ACCESS CONTROL =====");
        System.out.println("Access ID: " + accessId);
        System.out.println("User ID: " + userId);
        System.out.println("File ID: " + fileId);
        System.out.println("Read Permission: " + (permissionRead ? "Granted" : "Denied"));
        System.out.println("Write Permission: " + (permissionWrite ? "Granted" : "Denied"));
        System.out.println("Delete Permission: " + (permissionDelete ? "Granted" : "Denied"));
        System.out.println("Share Permission: " + (permissionShare ? "Granted" : "Denied"));
        System.out.println("Granted By: " + grantedBy);
        System.out.println("Granted Date: " + grantedDate);
        System.out.println("Expiry Date: " + expiryDate);
        System.out.println("Access Status: " + accessStatus);
        System.out.println("Access Level: " + accessLevel);
        System.out.println("Override Allowed: " + (overrideAllowed ? "Yes" : "No"));
        System.out.println("Policy Name: " + policyName);
        System.out.println("Remarks: " + remarks);
        System.out.println("==========================\n");
    }
    
    public boolean hasPermission(String permissionType) {
        try {
            if (permissionType.equals("read")) return permissionRead;
            if (permissionType.equals("write")) return permissionWrite;
            if (permissionType.equals("delete")) return permissionDelete;
            return false;
        } catch (Exception e) {
            System.err.println("Error checking permission: " + e.getMessage());
            return false;
        }
    }
}
