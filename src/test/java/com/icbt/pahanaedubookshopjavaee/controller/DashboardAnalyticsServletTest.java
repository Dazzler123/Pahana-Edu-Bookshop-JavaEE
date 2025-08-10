package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.dto.CustomerAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ItemAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.factory.ServiceFactory;
import com.icbt.pahanaedubookshopjavaee.service.DashboardService;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DashboardAnalyticsServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServiceFactory serviceFactory;

    @Mock
    private DashboardService dashboardService;

    @Mock
    private AbstractResponseUtility responseUtility;

    private DashboardAnalyticsServlet dashboardAnalyticsServlet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dashboardAnalyticsServlet = new DashboardAnalyticsServlet();

        // Use reflection to set private fields
        try {
            java.lang.reflect.Field serviceFactoryField = DashboardAnalyticsServlet.class.getSuperclass().getDeclaredField("serviceFactory");
            serviceFactoryField.setAccessible(true);
            serviceFactoryField.set(dashboardAnalyticsServlet, serviceFactory);

            java.lang.reflect.Field responseUtilityField = DashboardAnalyticsServlet.class.getSuperclass().getDeclaredField("abstractResponseUtility");
            responseUtilityField.setAccessible(true);
            responseUtilityField.set(dashboardAnalyticsServlet, responseUtility);

            java.lang.reflect.Field dashboardServiceField = DashboardAnalyticsServlet.class.getDeclaredField("dashboardService");
            dashboardServiceField.setAccessible(true);
            dashboardServiceField.set(dashboardAnalyticsServlet, dashboardService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testDoGet_MostVisitedCustomers_ShouldReturnCustomerAnalytics() throws IOException {
        // Arrange
        when(request.getParameter("type")).thenReturn("most-visited-customers");
        List<CustomerAnalyticsDTO> mockCustomers = Arrays.asList(
                new CustomerAnalyticsDTO("C001", "John Doe", 15, new BigDecimal("2500.00")),
                new CustomerAnalyticsDTO("C002", "Jane Smith", 12, new BigDecimal("1800.50"))
        );
        when(dashboardService.getMostVisitedCustomers()).thenReturn(mockCustomers);

        // Act
        dashboardAnalyticsServlet.doGet(request, response);

        // Assert
        verify(dashboardService).getMostVisitedCustomers();
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoGet_TopSellingItems_ShouldReturnItemAnalytics() throws IOException {
        // Arrange
        when(request.getParameter("type")).thenReturn("top-selling-items");
        List<ItemAnalyticsDTO> mockItems = Arrays.asList(
                new ItemAnalyticsDTO("I001", "Java Programming Book", 50, new BigDecimal("5000.00")),
                new ItemAnalyticsDTO("I002", "Python Guide", 35, new BigDecimal("3500.00"))
        );
        when(dashboardService.getTopSellingItems()).thenReturn(mockItems);

        // Act
        dashboardAnalyticsServlet.doGet(request, response);

        // Assert
        verify(dashboardService).getTopSellingItems();
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoGet_InvalidAnalyticsType_ShouldReturnBadRequest() throws IOException {
        // Arrange
        when(request.getParameter("type")).thenReturn("invalid-type");

        // Act
        dashboardAnalyticsServlet.doGet(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
        verify(dashboardService, never()).getMostVisitedCustomers();
        verify(dashboardService, never()).getTopSellingItems();
    }

    @Test
    public void testDoGet_MissingTypeParameter_ShouldReturnBadRequest() throws IOException {
        // Arrange
        when(request.getParameter("type")).thenReturn(null);

        // Act
        dashboardAnalyticsServlet.doGet(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
        verify(dashboardService, never()).getMostVisitedCustomers();
        verify(dashboardService, never()).getTopSellingItems();
    }

    @Test
    public void testDoGet_EmptyTypeParameter_ShouldReturnBadRequest() throws IOException {
        // Arrange
        when(request.getParameter("type")).thenReturn("");

        // Act
        dashboardAnalyticsServlet.doGet(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
        verify(dashboardService, never()).getMostVisitedCustomers();
        verify(dashboardService, never()).getTopSellingItems();
    }

    @Test
    public void testDoGet_ServiceThrowsException_ShouldReturnInternalServerError() throws IOException {
        // Arrange
        when(request.getParameter("type")).thenReturn("most-visited-customers");
        when(dashboardService.getMostVisitedCustomers()).thenThrow(new RuntimeException("Database error"));

        // Act
        dashboardAnalyticsServlet.doGet(request, response);

        // Assert
        verify(dashboardService).getMostVisitedCustomers();
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoGet_EmptyCustomerList_ShouldReturnValidResponse() throws IOException {
        // Arrange
        when(request.getParameter("type")).thenReturn("most-visited-customers");
        when(dashboardService.getMostVisitedCustomers()).thenReturn(Collections.emptyList());

        // Act
        dashboardAnalyticsServlet.doGet(request, response);

        // Assert
        verify(dashboardService).getMostVisitedCustomers();
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoGet_EmptyItemList_ShouldReturnValidResponse() throws IOException {
        // Arrange
        when(request.getParameter("type")).thenReturn("top-selling-items");
        when(dashboardService.getTopSellingItems()).thenReturn(Collections.emptyList());

        // Act
        dashboardAnalyticsServlet.doGet(request, response);

        // Assert
        verify(dashboardService).getTopSellingItems();
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoGet_NullCustomerList_ShouldHandleGracefully() throws IOException {
        // Arrange
        when(request.getParameter("type")).thenReturn("most-visited-customers");
        when(dashboardService.getMostVisitedCustomers()).thenReturn(null);

        // Act
        dashboardAnalyticsServlet.doGet(request, response);

        // Assert
        verify(dashboardService).getMostVisitedCustomers();
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }
}