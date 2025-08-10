package com.icbt.pahanaedubookshopjavaee.service.impl;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AuthServiceImplTest {

    private AuthServiceImpl authService;

    @Before
    public void setUp() {
        authService = new AuthServiceImpl();
    }

    @Test
    public void testAuthenticate_ValidCredentials_ShouldReturnTrue() {
        // Act
        boolean result = authService.authenticate("Admin", "1234");

        // Assert
        assertTrue("Should authenticate with valid credentials", result);
    }

    @Test
    public void testAuthenticate_InvalidUsername_ShouldReturnFalse() {
        // Act
        boolean result = authService.authenticate("InvalidUser", "1234");

        // Assert
        assertFalse("Should not authenticate with invalid username", result);
    }

    @Test
    public void testAuthenticate_InvalidPassword_ShouldReturnFalse() {
        // Act
        boolean result = authService.authenticate("Admin", "wrongpassword");

        // Assert
        assertFalse("Should not authenticate with invalid password", result);
    }

    @Test
    public void testAuthenticate_NullUsername_ShouldReturnFalse() {
        // Act
        boolean result = authService.authenticate(null, "1234");

        // Assert
        assertFalse("Should not authenticate with null username", result);
    }

    @Test
    public void testAuthenticate_NullPassword_ShouldReturnFalse() {
        // Act
        boolean result = authService.authenticate("Admin", null);

        // Assert
        assertFalse("Should not authenticate with null password", result);
    }

    @Test
    public void testAuthenticate_EmptyCredentials_ShouldReturnFalse() {
        // Act
        boolean result = authService.authenticate("", "");

        // Assert
        assertFalse("Should not authenticate with empty credentials", result);
    }

    @Test
    public void testAuthenticate_CaseSensitive_ShouldReturnFalse() {
        // Act
        boolean result = authService.authenticate("admin", "1234");

        // Assert
        assertFalse("Should be case sensitive for username", result);
    }

    @Test
    public void testAuthenticate_WhitespaceCredentials_ShouldReturnFalse() {
        // Act
        boolean result = authService.authenticate(" Admin ", " 1234 ");

        // Assert
        assertFalse("Should not authenticate with whitespace in credentials", result);
    }

}