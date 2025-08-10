package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.service.AuthService;
import com.icbt.pahanaedubookshopjavaee.util.constants.AuthConstants;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AuthServiceImplTest {

    private AuthService authService;

    @Before
    public void setUp() {
        authService = new AuthServiceImpl();
    }

    @Test
    public void testAuthenticate_ValidCredentials_ReturnsTrue() {
        // Arrange
        String validUsername = AuthConstants.ADMIN_USERNAME;
        String validPassword = AuthConstants.ADMIN_PASSWORD;

        // Act
        boolean result = authService.authenticate(validUsername, validPassword);

        // Assert
        assertTrue("Authentication should succeed with valid credentials", result);
    }

    @Test
    public void testAuthenticate_InvalidUsername_ReturnsFalse() {
        // Arrange
        String invalidUsername = "InvalidUser";
        String validPassword = AuthConstants.ADMIN_PASSWORD;

        // Act
        boolean result = authService.authenticate(invalidUsername, validPassword);

        // Assert
        assertFalse("Authentication should fail with invalid username", result);
    }

    @Test
    public void testAuthenticate_InvalidPassword_ReturnsFalse() {
        // Arrange
        String validUsername = AuthConstants.ADMIN_USERNAME;
        String invalidPassword = "wrongpassword";

        // Act
        boolean result = authService.authenticate(validUsername, invalidPassword);

        // Assert
        assertFalse("Authentication should fail with invalid password", result);
    }

    @Test
    public void testAuthenticate_BothInvalid_ReturnsFalse() {
        // Arrange
        String invalidUsername = "InvalidUser";
        String invalidPassword = "wrongpassword";

        // Act
        boolean result = authService.authenticate(invalidUsername, invalidPassword);

        // Assert
        assertFalse("Authentication should fail with both invalid credentials", result);
    }

    @Test
    public void testAuthenticate_NullUsername_ReturnsFalse() {
        // Arrange
        String nullUsername = null;
        String validPassword = AuthConstants.ADMIN_PASSWORD;

        // Act
        boolean result = authService.authenticate(nullUsername, validPassword);

        // Assert
        assertFalse("Authentication should fail with null username", result);
    }

    @Test
    public void testAuthenticate_NullPassword_ReturnsFalse() {
        // Arrange
        String validUsername = AuthConstants.ADMIN_USERNAME;
        String nullPassword = null;

        // Act
        boolean result = authService.authenticate(validUsername, nullPassword);

        // Assert
        assertFalse("Authentication should fail with null password", result);
    }

    @Test
    public void testAuthenticate_EmptyUsername_ReturnsFalse() {
        // Arrange
        String emptyUsername = "";
        String validPassword = AuthConstants.ADMIN_PASSWORD;

        // Act
        boolean result = authService.authenticate(emptyUsername, validPassword);

        // Assert
        assertFalse("Authentication should fail with empty username", result);
    }

    @Test
    public void testAuthenticate_EmptyPassword_ReturnsFalse() {
        // Arrange
        String validUsername = AuthConstants.ADMIN_USERNAME;
        String emptyPassword = "";

        // Act
        boolean result = authService.authenticate(validUsername, emptyPassword);

        // Assert
        assertFalse("Authentication should fail with empty password", result);
    }

    @Test
    public void testAuthenticate_WhitespaceUsername_ReturnsFalse() {
        // Arrange
        String whitespaceUsername = "   ";
        String validPassword = AuthConstants.ADMIN_PASSWORD;

        // Act
        boolean result = authService.authenticate(whitespaceUsername, validPassword);

        // Assert
        assertFalse("Authentication should fail with whitespace-only username", result);
    }

    @Test
    public void testAuthenticate_CaseSensitiveUsername_ReturnsFalse() {
        // Arrange
        String lowercaseUsername = AuthConstants.ADMIN_USERNAME.toLowerCase();
        String validPassword = AuthConstants.ADMIN_PASSWORD;

        // Act
        boolean result = authService.authenticate(lowercaseUsername, validPassword);

        // Assert
        assertFalse("Authentication should be case-sensitive for username", result);
    }

}