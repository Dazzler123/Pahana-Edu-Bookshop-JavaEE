package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.factory.ServiceFactory;
import com.icbt.pahanaedubookshopjavaee.service.ItemService;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServiceFactory serviceFactory;

    @Mock
    private ItemService itemService;

    @Mock
    private AbstractResponseUtility responseUtility;

    private ItemServlet itemServlet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        itemServlet = new ItemServlet();

        try {
            java.lang.reflect.Field serviceFactoryField = ItemServlet.class.getSuperclass().getDeclaredField("serviceFactory");
            serviceFactoryField.setAccessible(true);
            serviceFactoryField.set(itemServlet, serviceFactory);

            java.lang.reflect.Field responseUtilityField = ItemServlet.class.getSuperclass().getDeclaredField("abstractResponseUtility");
            responseUtilityField.setAccessible(true);
            responseUtilityField.set(itemServlet, responseUtility);

            java.lang.reflect.Field itemServiceField = ItemServlet.class.getDeclaredField("itemService");
            itemServiceField.setAccessible(true);
            itemServiceField.set(itemServlet, itemService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testDoGet_GenerateItemCode_ShouldReturnItemCode() throws IOException {
        // Arrange
        when(request.getParameter("action")).thenReturn("generateItemCode");
        JsonObject mockResponse = Json.createObjectBuilder().add("itemCode", "ITM-0001").build();
        when(itemService.generateNextItemCodeAsJson()).thenReturn(mockResponse);

        // Act
        itemServlet.doGet(request, response);

        // Assert
        verify(itemService).generateNextItemCodeAsJson();
        verify(responseUtility).writeJson(response, mockResponse);
    }

    @Test
    public void testDoGet_GetAllItems_ShouldReturnAllItems() throws IOException {
        // Arrange
        when(request.getParameter("action")).thenReturn(null);
        JsonObject mockResponse = Json.createObjectBuilder().add("state", "success").build();
        when(itemService.getAllItemsAsJson()).thenReturn(mockResponse);

        // Act
        itemServlet.doGet(request, response);

        // Assert
        verify(itemService).getAllItemsAsJson();
        verify(responseUtility).writeJson(response, mockResponse);
    }

    @Test
    public void testDoPost_ValidItemData_ShouldSaveItem() throws IOException {
        // Arrange
        when(request.getParameter("item_code")).thenReturn("ITM-0001");
        when(request.getParameter("name")).thenReturn("Java Book");
        when(request.getParameter("unit_price")).thenReturn("25.99");
        when(request.getParameter("qty_on_hand")).thenReturn("100");
        when(request.getParameter("status")).thenReturn("A");

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "done")
                .add("message", "Item saved successfully")
                .build();
        when(itemService.saveOrUpdateItem("ITM-0001", "Java Book", "25.99", "100", "A"))
                .thenReturn(mockResponse);

        // Act
        itemServlet.doPost(request, response);

        // Assert
        verify(itemService).saveOrUpdateItem("ITM-0001", "Java Book", "25.99", "100", "A");
        verify(responseUtility).writeJson(response, mockResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_ServiceReturnsError_ShouldSetBadRequestStatus() throws IOException {
        // Arrange
        when(request.getParameter("item_code")).thenReturn("ITM-0001");
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("unit_price")).thenReturn("25.99");
        when(request.getParameter("qty_on_hand")).thenReturn("100");
        when(request.getParameter("status")).thenReturn("A");

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Item name is required")
                .build();
        when(itemService.saveOrUpdateItem("ITM-0001", "", "25.99", "100", "A"))
                .thenReturn(errorResponse);

        // Act
        itemServlet.doPost(request, response);

        // Assert
        verify(itemService).saveOrUpdateItem("ITM-0001", "", "25.99", "100", "A");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPut_ValidStatusUpdate_ShouldUpdateStatus() throws IOException {
        // Arrange
        String jsonInput = "{\"item_code\":\"ITM-0001\",\"status\":\"I\"}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(jsonInput)));

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "success")
                .add("message", "Item status updated successfully")
                .build();
        when(itemService.updateItemStatus("ITM-0001", "I")).thenReturn(mockResponse);

        // Act
        itemServlet.doPut(request, response);

        // Assert
        verify(itemService).updateItemStatus("ITM-0001", "I");
        verify(responseUtility).writeJson(response, mockResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_ServiceReturnsError_ShouldSetBadRequestStatus() throws IOException {
        // Arrange
        String jsonInput = "{\"item_code\":\"ITM-0001\",\"status\":\"X\"}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(jsonInput)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Invalid status")
                .build();
        when(itemService.updateItemStatus("ITM-0001", "X")).thenReturn(errorResponse);

        // Act
        itemServlet.doPut(request, response);

        // Assert
        verify(itemService).updateItemStatus("ITM-0001", "X");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPut_MissingItemCode_ShouldUpdateWithNull() throws IOException {
        // Arrange
        String jsonInput = "{\"status\":\"I\"}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(jsonInput)));

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Item code is required")
                .build();
        when(itemService.updateItemStatus(null, "I")).thenReturn(mockResponse);

        // Act
        itemServlet.doPut(request, response);

        // Assert
        verify(itemService).updateItemStatus(null, "I");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_InvalidJson_ShouldHandleException() throws IOException {
        // Arrange
        String invalidJson = "{invalid json}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(invalidJson)));

        // Act
        itemServlet.doPut(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoPost_NullParameters_ShouldHandleGracefully() throws IOException {
        // Arrange
        when(request.getParameter("item_code")).thenReturn(null);
        when(request.getParameter("name")).thenReturn(null);
        when(request.getParameter("unit_price")).thenReturn(null);
        when(request.getParameter("qty_on_hand")).thenReturn(null);
        when(request.getParameter("status")).thenReturn(null);

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Required parameters missing")
                .build();
        when(itemService.saveOrUpdateItem(null, null, null, null, null))
                .thenReturn(mockResponse);

        // Act
        itemServlet.doPost(request, response);

        // Assert
        verify(itemService).saveOrUpdateItem(null, null, null, null, null);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}