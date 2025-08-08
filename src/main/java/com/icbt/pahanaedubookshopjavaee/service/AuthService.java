package com.icbt.pahanaedubookshopjavaee.service;

public interface AuthService {
    boolean authenticate(String username, String password);
}