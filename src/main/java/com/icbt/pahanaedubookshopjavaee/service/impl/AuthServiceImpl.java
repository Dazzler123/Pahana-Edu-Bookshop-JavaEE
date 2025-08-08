package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.service.AuthService;
import com.icbt.pahanaedubookshopjavaee.util.constants.AuthConstants;

public class AuthServiceImpl implements AuthService {

    @Override
    public boolean authenticate(String username, String password) {
        return AuthConstants.ADMIN_USERNAME.equals(username) && 
               AuthConstants.ADMIN_PASSWORD.equals(password);
    }
}