package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.factory.ServiceFactory;
import com.icbt.pahanaedubookshopjavaee.service.PlaceOrderService;
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
public class PlaceOrderServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServiceFactory serviceFactory;

    @Mock
    private PlaceOrderService placeOrderService;

    @Mock
    private AbstractResponseUtility responseUtility;

    private PlaceOrderServlet placeOrderServlet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        placeOrderServlet = new PlaceOrderServlet();

        try {
            java.lang.reflect.Field serviceFactoryField = PlaceOrderServlet.class.getSuperclass().getDeclaredField("serviceFactory");
            serviceFactoryField.setAccessible(true);
            serviceFactoryField.set(placeOrderServlet, serviceFactory);

            java.lang.reflect.Field responseUtilityField = PlaceOrderServlet.class.getSuperclass().getDeclaredField("abstractResponseUtility");
            responseUtilityField.setAccessible(true);
            responseUtilityField.set(placeOrderServlet, responseUtility);

            java.lang.reflect.Field placeOrderServiceField = PlaceOrderServlet.class.getDeclaredField("placeOrderService");
            placeOrderServiceField.setAccessible(true);
            placeOrderServiceField.set(placeOrderServlet, placeOrderService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testDoPost_ValidOrderRequest_ShouldPlaceOrder() throws IOException {
        // Arrange
        String validOrderJson = "{"
                + "\"customerAccount\":\"C001\","
                + "\"paymentMethod\":\"CASH\","
                + "\"items\":[{\"itemCode\":\"ITM-001\",\"quantity\":2,\"unitPrice\":25.99}]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(validOrderJson)));

        JsonObject successResponse = Json.createObjectBuilder()
                .add("state", "done")
                .add("message", "Order placed successfully")
                .add("orderCode", "ORD-001")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(successResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(placeOrderService).processOrderRequest(any(JsonObject.class));
        verify(responseUtility).writeJson(response, successResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_ServiceReturnsError_ShouldSetBadRequestStatus() throws IOException {
        // Arrange
        String invalidOrderJson = "{"
                + "\"customerAccount\":\"\","
                + "\"paymentMethod\":\"CASH\","
                + "\"items\":[]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(invalidOrderJson)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Customer account is required")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(errorResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(placeOrderService).processOrderRequest(any(JsonObject.class));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPost_InvalidJson_ShouldHandleException() throws IOException {
        // Arrange
        String invalidJson = "{invalid json format}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(invalidJson)));

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_EmptyOrderItems_ShouldReturnError() throws IOException {
        // Arrange
        String emptyItemsJson = "{"
                + "\"customerAccount\":\"C001\","
                + "\"paymentMethod\":\"CASH\","
                + "\"items\":[]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(emptyItemsJson)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Order must contain at least one item")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(errorResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(placeOrderService).processOrderRequest(any(JsonObject.class));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_MissingCustomerAccount_ShouldReturnError() throws IOException {
        // Arrange
        String jsonWithoutCustomer = "{"
                + "\"paymentMethod\":\"CASH\","
                + "\"items\":[{\"itemCode\":\"ITM-001\",\"quantity\":2,\"unitPrice\":25.99}]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(jsonWithoutCustomer)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Customer account is required")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(errorResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPost_MissingPaymentMethod_ShouldReturnError() throws IOException {
        // Arrange
        String jsonWithoutPayment = "{"
                + "\"customerAccount\":\"C001\","
                + "\"items\":[{\"itemCode\":\"ITM-001\",\"quantity\":2,\"unitPrice\":25.99}]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(jsonWithoutPayment)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Payment method is required")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(errorResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPost_InvalidPaymentMethod_ShouldReturnError() throws IOException {
        // Arrange
        String invalidPaymentJson = "{"
                + "\"customerAccount\":\"C001\","
                + "\"paymentMethod\":\"BITCOIN\","
                + "\"items\":[{\"itemCode\":\"ITM-001\",\"quantity\":2,\"unitPrice\":25.99}]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(invalidPaymentJson)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Invalid payment method. Allowed: Cash, Card, Other")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(errorResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPost_InvalidItemQuantity_ShouldReturnError() throws IOException {
        // Arrange
        String invalidQuantityJson = "{"
                + "\"customerAccount\":\"C001\","
                + "\"paymentMethod\":\"CASH\","
                + "\"items\":[{\"itemCode\":\"ITM-001\",\"quantity\":0,\"unitPrice\":25.99}]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(invalidQuantityJson)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Quantity must be greater than zero for item: ITM-001")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(errorResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPost_InvalidItemPrice_ShouldReturnError() throws IOException {
        // Arrange
        String invalidPriceJson = "{"
                + "\"customerAccount\":\"C001\","
                + "\"paymentMethod\":\"CASH\","
                + "\"items\":[{\"itemCode\":\"ITM-001\",\"quantity\":2,\"unitPrice\":-5.99}]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(invalidPriceJson)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Unit price must be greater than zero for item: ITM-001")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(errorResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPost_MultipleItems_ShouldPlaceOrder() throws IOException {
        // Arrange
        String multiItemJson = "{"
                + "\"customerAccount\":\"C001\","
                + "\"paymentMethod\":\"CARD\","
                + "\"items\":["
                + "{\"itemCode\":\"ITM-001\",\"quantity\":2,\"unitPrice\":25.99},"
                + "{\"itemCode\":\"ITM-002\",\"quantity\":1,\"unitPrice\":15.50}"
                + "]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(multiItemJson)));

        JsonObject successResponse = Json.createObjectBuilder()
                .add("state", "done")
                .add("message", "Order placed successfully")
                .add("orderCode", "ORD-002")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(successResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(placeOrderService).processOrderRequest(any(JsonObject.class));
        verify(responseUtility).writeJson(response, successResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_ServiceThrowsException_ShouldReturnInternalServerError() throws IOException {
        // Arrange
        String validOrderJson = "{"
                + "\"customerAccount\":\"C001\","
                + "\"paymentMethod\":\"CASH\","
                + "\"items\":[{\"itemCode\":\"ITM-001\",\"quantity\":2,\"unitPrice\":25.99}]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(validOrderJson)));
        when(placeOrderService.processOrderRequest(any(JsonObject.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_EmptyJsonObject_ShouldReturnError() throws IOException {
        // Arrange
        String emptyJson = "{}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(emptyJson)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Customer account is required")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(errorResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPost_MissingItemCode_ShouldReturnError() throws IOException {
        // Arrange
        String missingItemCodeJson = "{"
                + "\"customerAccount\":\"C001\","
                + "\"paymentMethod\":\"CASH\","
                + "\"items\":[{\"quantity\":2,\"unitPrice\":25.99}]"
                + "}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(missingItemCodeJson)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Item code is required for order item")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(errorResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPut_ShouldReturnMethodNotAllowed() throws IOException {
        // Act
        placeOrderServlet.doPut(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoGet_ShouldReturnMethodNotAllowed() throws IOException {
        // Act
        placeOrderServlet.doGet(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_NullJsonReader_ShouldHandleException() throws IOException {
        // Arrange
        when(request.getReader()).thenThrow(new IOException("Reader unavailable"));

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_LargeOrderWithManyItems_ShouldPlaceOrder() throws IOException {
        // Arrange
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{")
                .append("\"customerAccount\":\"C001\",")
                .append("\"paymentMethod\":\"CARD\",")
                .append("\"items\":[");
        
        for (int i = 1; i <= 10; i++) {
            if (i > 1) jsonBuilder.append(",");
            jsonBuilder.append("{\"itemCode\":\"ITM-00").append(i).append("\",")
                    .append("\"quantity\":").append(i).append(",")
                    .append("\"unitPrice\":").append(10.99 + i).append("}");
        }
        jsonBuilder.append("]}");

        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(jsonBuilder.toString())));

        JsonObject successResponse = Json.createObjectBuilder()
                .add("state", "done")
                .add("message", "Order placed successfully")
                .add("orderCode", "ORD-003")
                .build();
        when(placeOrderService.processOrderRequest(any(JsonObject.class))).thenReturn(successResponse);

        // Act
        placeOrderServlet.doPost(request, response);

        // Assert
        verify(placeOrderService).processOrderRequest(any(JsonObject.class));
        verify(responseUtility).writeJson(response, successResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
