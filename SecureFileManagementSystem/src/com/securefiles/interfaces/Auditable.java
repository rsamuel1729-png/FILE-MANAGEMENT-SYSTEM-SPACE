package com.securefiles.interfaces;

public interface Auditable {
    void logAction(String action, String description);
}
