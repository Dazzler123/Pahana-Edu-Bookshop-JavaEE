package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.dto.DashboardStatsDTO;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DashboardStatsServletTest {

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

    private DashboardStatsServlet dashboardStatsServlet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dashboardStatsServlet = new DashboardStatsServlet();

        // Use reflection to set private fields
        try {
            java.lang.reflect.Field serviceFactoryField = DashboardStatsServlet.class.getSuperclass().getDeclaredField("serviceFactory");
            serviceFactoryField.setAccessible(true);
            serviceFactoryField.set(dashboardStatsServlet, serviceFactory);

            java.lang.reflect.Field responseUtilityField = DashboardStatsServlet.class.getSuperclass().getDeclaredField("abstractResponseUtility");
            responseUtilityField.setAccessible(true);
            responseUtilityField.set(dashboardStatsServlet, responseUtility);

            java.lang.reflect.Field dashboardServiceField = DashboardStatsServlet.class.getDeclaredField("dashboardService");
            dashboardServiceField.setAccessible(true);
            dashboardServiceField.set(dashboardStatsServlet, dashboardService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testDoGet_ValidRequest_ShouldReturnDashboardStats() throws IOException {
        // Arrange
        DashboardStatsDTO mockStats = new DashboardStatsDTO(100, 25, 50000.75);
        when(dashboardService.getDashboardStats()).thenReturn(mockStats);

        // Act
        dashboardStatsServlet.doGet(request, response);

        // Assert
        verify(dashboardService).getDashboardStats();
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoGet_ServiceThrowsException_ShouldReturnInternalServerError() throws IOException {
        // Arrange
        when(dashboardService.getDashboardStats()).thenThrow(new RuntimeException("Database connection failed"));

        // Act
        dashboardStatsServlet.doGet(request, response);

        // Assert
        verify(dashboardService).getDashboardStats();
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoGet_ServiceReturnsNull_ShouldHandleGracefully() throws IOException {
        // Arrange
        when(dashboardService.getDashboardStats()).thenReturn(null);

        // Act
        dashboardStatsServlet.doGet(request, response);

        // Assert
        verify(dashboardService).getDashboardStats();
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
    }

    @Test
    public void testDoGet_ServiceReturnsZeroStats_ShouldReturnValidResponse() throws IOException {
        // Arrange
        DashboardStatsDTO mockStats = new DashboardStatsDTO(0, 0, 0.0);
        when(dashboardService.getDashboardStats()).thenReturn(mockStats);

        // Act
        dashboardStatsServlet.doGet(request, response);

        // Assert
        verify(dashboardService).getDashboardStats();
        verify(responseUtility).writeJson(eq(response), any(JsonObject.class));
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}