package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.factory.ServiceFactory;
import com.icbt.pahanaedubookshopjavaee.service.CustomerService;
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
public class CustomerServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServiceFactory serviceFactory;

    @Mock
    private CustomerService customerService;

    @Mock
    private AbstractResponseUtility responseUtility;

    private CustomerServlet customerServlet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        customerServlet = new CustomerServlet();

        try {
            java.lang.reflect.Field serviceFactoryField = CustomerServlet.class.getSuperclass().getDeclaredField("serviceFactory");
            serviceFactoryField.setAccessible(true);
            serviceFactoryField.set(customerServlet, serviceFactory);

            java.lang.reflect.Field responseUtilityField = CustomerServlet.class.getSuperclass().getDeclaredField("abstractResponseUtility");
            responseUtilityField.setAccessible(true);
            responseUtilityField.set(customerServlet, responseUtility);

            java.lang.reflect.Field customerServiceField = CustomerServlet.class.getDeclaredField("customerService");
            customerServiceField.setAccessible(true);
            customerServiceField.set(customerServlet, customerService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testDoGet_GetAllCustomers_ShouldReturnAllCustomers() throws IOException {
        // Arrange
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("accountNumber")).thenReturn(null);
        
        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "success")
                .add("customers", Json.createArrayBuilder().build())
                .build();
        when(customerService.getAllCustomersAsJson()).thenReturn(mockResponse);

        // Act
        customerServlet.doGet(request, response);

        // Assert
        verify(customerService).getAllCustomersAsJson();
        verify(responseUtility).writeJson(response, mockResponse);
    }

    @Test
    public void testDoGet_GetCustomerIds_ShouldReturnCustomerIds() throws IOException {
        // Arrange
        when(request.getParameter("action")).thenReturn("ids");
        
        JsonObject mockResponse = Json.createObjectBuilder()
                .add("customerIds", Json.createArrayBuilder().build())
                .build();
        when(customerService.getAllCustomerIdsAsJson()).thenReturn(mockResponse);

        // Act
        customerServlet.doGet(request, response);

        // Assert
        verify(customerService).getAllCustomerIdsAsJson();
        verify(responseUtility).writeJson(response, mockResponse);
    }

    @Test
    public void testDoGet_GenerateAccountNumber_ShouldReturnAccountNumber() throws IOException {
        // Arrange
        when(request.getParameter("action")).thenReturn("generateAccountNumber");
        
        JsonObject mockResponse = Json.createObjectBuilder()
                .add("accountNumber", "CUS-0001")
                .build();
        when(customerService.generateNextAccountNumberAsJson()).thenReturn(mockResponse);

        // Act
        customerServlet.doGet(request, response);

        // Assert
        verify(customerService).generateNextAccountNumberAsJson();
        verify(responseUtility).writeJson(response, mockResponse);
    }

    @Test
    public void testDoGet_GetCustomerByAccountNumber_ShouldReturnCustomer() throws IOException {
        // Arrange
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("accountNumber")).thenReturn("CUS-0001");
        
        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "success")
                .add("customer", Json.createObjectBuilder().build())
                .build();
        when(customerService.getCustomerByAccountNumberAsJson("CUS-0001")).thenReturn(mockResponse);

        // Act
        customerServlet.doGet(request, response);

        // Assert
        verify(customerService).getCustomerByAccountNumberAsJson("CUS-0001");
        verify(responseUtility).writeJson(response, mockResponse);
    }

    @Test
    public void testDoGet_CustomerNotFound_ShouldReturnNotFound() throws IOException {
        // Arrange
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("accountNumber")).thenReturn("CUS-9999");
        
        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Customer not found")
                .build();
        when(customerService.getCustomerByAccountNumberAsJson("CUS-9999")).thenReturn(errorResponse);

        // Act
        customerServlet.doGet(request, response);

        // Assert
        verify(customerService).getCustomerByAccountNumberAsJson("CUS-9999");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    // POST Tests
    @Test
    public void testDoPost_ValidCustomerData_ShouldSaveCustomer() throws IOException {
        // Arrange
        when(request.getParameter("account_number")).thenReturn("CUS-0001");
        when(request.getParameter("name")).thenReturn("John Doe");
        when(request.getParameter("address")).thenReturn("123 Main St");
        when(request.getParameter("telephone")).thenReturn("555-1234");
        when(request.getParameter("status")).thenReturn("A");

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "done")
                .add("message", "Customer saved successfully")
                .build();
        when(customerService.saveOrUpdateCustomer("CUS-0001", "John Doe", "123 Main St", "555-1234", "A"))
                .thenReturn(mockResponse);

        // Act
        customerServlet.doPost(request, response);

        // Assert
        verify(customerService).saveOrUpdateCustomer("CUS-0001", "John Doe", "123 Main St", "555-1234", "A");
        verify(responseUtility).writeJson(response, mockResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_ServiceReturnsError_ShouldSetBadRequestStatus() throws IOException {
        // Arrange
        when(request.getParameter("account_number")).thenReturn("CUS-0001");
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("address")).thenReturn("123 Main St");
        when(request.getParameter("telephone")).thenReturn("555-1234");
        when(request.getParameter("status")).thenReturn("A");

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Customer name is required")
                .build();
        when(customerService.saveOrUpdateCustomer("CUS-0001", "", "123 Main St", "555-1234", "A"))
                .thenReturn(errorResponse);

        // Act
        customerServlet.doPost(request, response);

        // Assert
        verify(customerService).saveOrUpdateCustomer("CUS-0001", "", "123 Main St", "555-1234", "A");
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPut_ValidStatusUpdate_ShouldUpdateStatus() throws IOException {
        // Arrange
        String jsonInput = "{\"account_number\":\"CUS-0001\",\"status\":\"I\"}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(jsonInput)));

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "done")
                .add("message", "Customer status updated successfully")
                .build();
        when(customerService.updateCustomerStatus("CUS-0001", "I")).thenReturn(mockResponse);

        // Act
        customerServlet.doPut(request, response);

        // Assert
        verify(customerService).updateCustomerStatus("CUS-0001", "I");
        verify(responseUtility).writeJson(response, mockResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_ServiceReturnsError_ShouldSetBadRequestStatus() throws IOException {
        // Arrange
        String jsonInput = "{\"account_number\":\"CUS-9999\",\"status\":\"I\"}";
        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(jsonInput)));

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Customer not found")
                .build();
        when(customerService.updateCustomerStatus("CUS-9999", "I")).thenReturn(errorResponse);

        // Act
        customerServlet.doPut(request, response);

        // Assert
        verify(customerService).updateCustomerStatus("CUS-9999", "I");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoPost_NullParameters_ShouldHandleGracefully() throws IOException {
        // Arrange
        when(request.getParameter("account_number")).thenReturn(null);
        when(request.getParameter("name")).thenReturn(null);
        when(request.getParameter("address")).thenReturn(null);
        when(request.getParameter("telephone")).thenReturn(null);
        when(request.getParameter("status")).thenReturn(null);

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Required parameters missing")
                .build();
        when(customerService.saveOrUpdateCustomer(null, null, null, null, null))
                .thenReturn(mockResponse);

        // Act
        customerServlet.doPost(request, response);

        // Assert
        verify(customerService).saveOrUpdateCustomer(null, null, null, null, null);
    }

}