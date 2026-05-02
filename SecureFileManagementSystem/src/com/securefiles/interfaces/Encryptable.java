package com.securefiles.interfaces;

public interface Encryptable {
    String encrypt(String data);
    String decrypt(String data);
}
