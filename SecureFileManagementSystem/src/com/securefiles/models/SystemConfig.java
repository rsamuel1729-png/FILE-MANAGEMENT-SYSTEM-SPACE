package com.securefiles.models;

public class SystemConfig {
    private String configId;
    private String systemName;
    private String version;
    private String environment;
    private int maxUsers;
    private long maxFileSize;
    private String allowedFileTypes;
    private String defaultEncryptionProfile;
    private String passwordPolicy;
    private int sessionTimeout;
    private String loggingLevel;
    private String backupPolicy;
    private boolean maintenanceMode;
    private String complianceStandard;
    private String lastUpdated;
    
    public SystemConfig() {
        this.configId = "CONFIG-001";
        this.systemName = "Secure File Management System";
        this.version = "1.0.0";
        this.environment = "Production Environment";
        this.maxUsers = 100;
        this.maxFileSize = 10485760;
        this.allowedFileTypes = "txt, doc, pdf, jpg, png";
        this.defaultEncryptionProfile = "AES-256 Encryption";
        this.passwordPolicy = "Strong - Min 8 chars with special characters";
        this.sessionTimeout = 3600;
        this.loggingLevel = "INFO Level Logging";
        this.backupPolicy = "Daily Automatic Backup";
        this.maintenanceMode = false;
        this.complianceStandard = "ISO27001 Security Standard";
        this.lastUpdated = "2025-02-09";
    }
    
    public void displaySystemInfo() {
        System.out.println("\n========== SYSTEM CONFIGURATION ==========");
        System.out.println("Config ID: " + configId);
        System.out.println("System Name: " + systemName);
        System.out.println("Version: " + version);
        System.out.println("Environment: " + environment);
        System.out.println("Max Users: " + maxUsers);
        System.out.println("Max File Size: " + maxFileSize + " bytes");
        System.out.println("Allowed File Types: " + allowedFileTypes);
        System.out.println("Default Encryption: " + defaultEncryptionProfile);
        System.out.println("Password Policy: " + passwordPolicy);
        System.out.println("Session Timeout: " + sessionTimeout + " seconds");
        System.out.println("Logging Level: " + loggingLevel);
        System.out.println("Backup Policy: " + backupPolicy);
        System.out.println("Maintenance Mode: " + (maintenanceMode ? "ON" : "OFF"));
        System.out.println("Compliance Standard: " + complianceStandard);
        System.out.println("Last Updated: " + lastUpdated);
        System.out.println("==========================================\n");
    }
    
    public String getSystemName() {
        return systemName;
    }
}
