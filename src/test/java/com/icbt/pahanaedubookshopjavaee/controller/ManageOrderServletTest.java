package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.factory.ServiceFactory;
import com.icbt.pahanaedubookshopjavaee.service.OrderManagementService;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ManageOrderServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServiceFactory serviceFactory;

    @Mock
    private OrderManagementService orderManagementService;

    @Mock
    private AbstractResponseUtility responseUtility;

    private ManageOrderServlet manageOrderServlet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        manageOrderServlet = new ManageOrderServlet();

        try {
            java.lang.reflect.Field serviceFactoryField = ManageOrderServlet.class.getSuperclass().getDeclaredField("serviceFactory");
            serviceFactoryField.setAccessible(true);
            serviceFactoryField.set(manageOrderServlet, serviceFactory);

            java.lang.reflect.Field responseUtilityField = ManageOrderServlet.class.getSuperclass().getDeclaredField("abstractResponseUtility");
            responseUtilityField.setAccessible(true);
            responseUtilityField.set(manageOrderServlet, responseUtility);

            java.lang.reflect.Field orderServiceField = ManageOrderServlet.class.getDeclaredField("orderManagementService");
            orderServiceField.setAccessible(true);
            orderServiceField.set(manageOrderServlet, orderManagementService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testDoPost_ValidOrderUpdate_ShouldUpdateOrder() throws IOException {
        // Arrange
        String updateOrderJson = "{"
                + "\"orderCode\":\"ORD-001\","
                + "\"customerId\":\"CUS-001\","
                + "\"totalAmount\":150.00,"
                + "\"items\":[{\"itemCode\":\"ITM-001\",\"quantity\":3}]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(updateOrderJson)));

        JsonObject successResponse = Json.createObjectBuilder()
                .add("state", "success")
                .add("message", "Order updated successfully")
                .build();
        when(orderManagementService.processUpdateOrderRequest(any(JsonObject.class))).thenReturn(successResponse);

        // Act
        manageOrderServlet.doPost(request, response);

        // Assert
        verify(orderManagementService).processUpdateOrderRequest(any(JsonObject.class));
        verify(responseUtility).writeJson(response, successResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_ServiceReturnsError_ShouldSetBadRequestStatus() throws IOException {
        // Arrange
        String invalidOrderJson = "{"
                + "\"orderCode\":\"\","
                + "\"customerId\":\"CUS-001\","
                + "\"totalAmount\":150.00"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(invalidOrderJson)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Order code is required")
                .build();
        when(orderManagementService.processUpdateOrderRequest(any(JsonObject.class))).thenReturn(errorResponse);

        // Act
        manageOrderServlet.doPost(request, response);

        // Assert
        verify(orderManagementService).processUpdateOrderRequest(any(JsonObject.class));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPut_ValidStatusUpdate_ShouldUpdateStatus() throws IOException {
        // Arrange
        String statusUpdateJson = "{\"orderCode\":\"ORD-001\",\"status\":\"A\"}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(statusUpdateJson)));

        JsonObject successResponse = Json.createObjectBuilder()
                .add("state", "success")
                .add("message", "Order status updated successfully")
                .build();
        when(orderManagementService.processUpdateOrderStatusRequest(any(JsonObject.class))).thenReturn(successResponse);

        // Act
        manageOrderServlet.doPut(request, response);

        // Assert
        verify(orderManagementService).processUpdateOrderStatusRequest(any(JsonObject.class));
        verify(responseUtility).writeJson(response, successResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_InvalidStatus_ShouldReturnError() throws IOException {
        // Arrange
        String invalidStatusJson = "{\"orderCode\":\"ORD-001\",\"status\":\"X\"}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(invalidStatusJson)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Invalid order status")
                .build();
        when(orderManagementService.processUpdateOrderStatusRequest(any(JsonObject.class))).thenReturn(errorResponse);

        // Act
        manageOrderServlet.doPut(request, response);

        // Assert
        verify(orderManagementService).processUpdateOrderStatusRequest(any(JsonObject.class));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPost_InvalidJson_ShouldHandleException() throws IOException {
        // Arrange
        String invalidJson = "{invalid json}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(invalidJson)));

        // Act
        manageOrderServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPut_InvalidJson_ShouldHandleException() throws IOException {
        // Arrange
        String invalidJson = "{invalid json}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(invalidJson)));

        // Act
        manageOrderServlet.doPut(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

}