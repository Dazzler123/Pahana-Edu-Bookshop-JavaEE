package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.factory.ServiceFactory;
import com.icbt.pahanaedubookshopjavaee.service.AuthService;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoginServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServiceFactory serviceFactory;

    @Mock
    private AuthService authService;

    @Mock
    private AbstractResponseUtility responseUtility;

    private LoginServlet loginServlet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loginServlet = new LoginServlet();

        // Use reflection to set private fields
        try {
            java.lang.reflect.Field serviceFactoryField = LoginServlet.class.getSuperclass().getDeclaredField("serviceFactory");
            serviceFactoryField.setAccessible(true);
            serviceFactoryField.set(loginServlet, serviceFactory);

            java.lang.reflect.Field responseUtilityField = LoginServlet.class.getSuperclass().getDeclaredField("abstractResponseUtility");
            responseUtilityField.setAccessible(true);
            responseUtilityField.set(loginServlet, responseUtility);

            // Set the authService field directly in LoginServlet
            java.lang.reflect.Field authServiceField = LoginServlet.class.getDeclaredField("authService");
            authServiceField.setAccessible(true);
            authServiceField.set(loginServlet, authService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testDoPost_ValidCredentials_Success() throws IOException {
        // Arrange
        String jsonInput = "{\"username\":\"Admin\",\"password\":\"1234\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(request.getReader()).thenReturn(reader);
        when(request.getSession()).thenReturn(session);
        when(authService.authenticate("Admin", "1234")).thenReturn(true);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(session).setAttribute("username", "Admin");
        verify(session).setAttribute("isLoggedIn", true);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoPost_InvalidCredentials_Unauthorized() throws IOException {
        // Arrange
        String jsonInput = "{\"username\":\"Admin\",\"password\":\"wrong\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(request.getReader()).thenReturn(reader);
        when(authService.authenticate("Admin", "wrong")).thenReturn(false);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
        verify(request, never()).getSession();
    }

    @Test
    public void testDoPost_EmptyUsername_Unauthorized() throws IOException {
        // Arrange
        String jsonInput = "{\"username\":\"\",\"password\":\"1234\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(request.getReader()).thenReturn(reader);
        when(authService.authenticate("", "1234")).thenReturn(false);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_EmptyPassword_Unauthorized() throws IOException {
        // Arrange
        String jsonInput = "{\"username\":\"Admin\",\"password\":\"\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(request.getReader()).thenReturn(reader);
        when(authService.authenticate("Admin", "")).thenReturn(false);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_MissingUsernameField_Unauthorized() throws IOException {
        // Arrange
        String jsonInput = "{\"password\":\"1234\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(request.getReader()).thenReturn(reader);
        when(authService.authenticate("", "1234")).thenReturn(false);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_MissingPasswordField_Unauthorized() throws IOException {
        // Arrange
        String jsonInput = "{\"username\":\"Admin\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(request.getReader()).thenReturn(reader);
        when(authService.authenticate("Admin", "")).thenReturn(false);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_InvalidJson_InternalServerError() throws IOException {
        // Arrange
        String invalidJson = "{invalid json}";
        BufferedReader reader = new BufferedReader(new StringReader(invalidJson));

        when(request.getReader()).thenReturn(reader);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_ExceptionDuringAuthentication_InternalServerError() throws IOException {
        // Arrange
        String jsonInput = "{\"username\":\"Admin\",\"password\":\"1234\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(request.getReader()).thenReturn(reader);
        when(authService.authenticate(anyString(), anyString())).thenThrow(new RuntimeException("Database error"));

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_SessionCreation_ValidCredentials() throws IOException {
        // Arrange
        String jsonInput = "{\"username\":\"Admin\",\"password\":\"1234\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(request.getReader()).thenReturn(reader);
        when(request.getSession()).thenReturn(session);
        when(authService.authenticate("Admin", "1234")).thenReturn(true);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(request).getSession();
        verify(session).setAttribute("username", "Admin");
        verify(session).setAttribute("isLoggedIn", true);
    }

    @Test
    public void testDoPost_NullJsonInput_InternalServerError() throws IOException {
        // Arrange
        BufferedReader reader = new BufferedReader(new StringReader(""));

        when(request.getReader()).thenReturn(reader);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_WhitespaceCredentials_Unauthorized() throws IOException {
        // Arrange
        String jsonInput = "{\"username\":\"   \",\"password\":\"   \"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(request.getReader()).thenReturn(reader);
        when(authService.authenticate("   ", "   ")).thenReturn(false);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

}
