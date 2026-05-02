package com.securefiles.interfaces;

public interface Authenticatable {
    boolean authenticate(String username, String password);
    void logout();
}
