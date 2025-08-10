package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.DashboardDAO;
import com.icbt.pahanaedubookshopjavaee.dto.CustomerAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.DashboardStatsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ItemAnalyticsDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DashboardServiceImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private DashboardDAO dashboardDAO;

    private DashboardServiceImpl dashboardService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dashboardService = new DashboardServiceImpl(dataSource);
        
        // Inject mock DAO using reflection
        try {
            java.lang.reflect.Field dashboardDAOField = DashboardServiceImpl.class.getDeclaredField("dashboardDAO");
            dashboardDAOField.setAccessible(true);
            dashboardDAOField.set(dashboardService, dashboardDAO);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock DAO", e);
        }
    }

    @Test
    public void testGetDashboardStats_ShouldReturnStats() throws Exception {
        // Arrange
        DashboardStatsDTO mockStats = new DashboardStatsDTO(100, 15, 5000.00);
        
        when(dashboardDAO.getDashboardStats()).thenReturn(mockStats);

        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // Assert
        assertNotNull("Should return dashboard stats", result);
        assertEquals("Should return correct total orders", 100, result.getTotalOrders());
        assertEquals("Should return correct pending orders", 15, result.getPendingOrders());
        assertEquals("Should return correct total revenue", 5000.00, result.getTotalRevenue(), 0.01);
        verify(dashboardDAO).getDashboardStats();
    }

    @Test
    public void testGetDashboardStats_DAOThrowsException_ShouldThrowRuntimeException() {
        // Arrange
        when(dashboardDAO.getDashboardStats()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        try {
            dashboardService.getDashboardStats();
            fail("Should throw RuntimeException");
        } catch (RuntimeException e) {
            assertTrue("Should contain error message", e.getMessage().contains("Failed to load dashboard statistics"));
            verify(dashboardDAO).getDashboardStats();
        }
    }

    @Test
    public void testGetMostVisitedCustomers_ShouldReturnCustomers() throws Exception {
        // Arrange
        List<CustomerAnalyticsDTO> mockCustomers = Arrays.asList(
            new CustomerAnalyticsDTO("CUS-001", "John Doe", 25, new BigDecimal("1500.00")),
            new CustomerAnalyticsDTO("CUS-002", "Jane Smith", 20, new BigDecimal("1200.00"))
        );
        when(dashboardDAO.getMostVisitedCustomers(5)).thenReturn(mockCustomers);

        // Act
        List<CustomerAnalyticsDTO> result = dashboardService.getMostVisitedCustomers();

        // Assert
        assertNotNull("Should return customer list", result);
        assertEquals("Should return correct number of customers", 2, result.size());
        assertEquals("First customer should be John Doe", "John Doe", result.get(0).getName());
        verify(dashboardDAO).getMostVisitedCustomers(5);
    }

    @Test
    public void testGetMostVisitedCustomers_DAOThrowsException_ShouldThrowRuntimeException() {
        // Arrange
        when(dashboardDAO.getMostVisitedCustomers(anyInt())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        try {
            dashboardService.getMostVisitedCustomers();
            fail("Should throw RuntimeException");
        } catch (RuntimeException e) {
            assertTrue("Should contain error message", e.getMessage().contains("Failed to load most visited customers"));
            verify(dashboardDAO).getMostVisitedCustomers(5);
        }
    }

    @Test
    public void testGetTopSellingItems_ShouldReturnItems() throws Exception {
        // Arrange
        List<ItemAnalyticsDTO> mockItems = Arrays.asList(
            new ItemAnalyticsDTO("ITM-001", "Book 1", 50, new BigDecimal("2500.00")),
            new ItemAnalyticsDTO("ITM-002", "Book 2", 35, new BigDecimal("1750.00"))
        );
        when(dashboardDAO.getTopSellingItems(5)).thenReturn(mockItems);

        // Act
        List<ItemAnalyticsDTO> result = dashboardService.getTopSellingItems();

        // Assert
        assertNotNull("Should return item list", result);
        assertEquals("Should return correct number of items", 2, result.size());
        assertEquals("First item should be Book 1", "Book 1", result.get(0).getName());
        verify(dashboardDAO).getTopSellingItems(5);
    }

    @Test
    public void testGetTopSellingItems_DAOThrowsException_ShouldThrowRuntimeException() {
        // Arrange
        when(dashboardDAO.getTopSellingItems(anyInt())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        try {
            dashboardService.getTopSellingItems();
            fail("Should throw RuntimeException");
        } catch (RuntimeException e) {
            assertTrue("Should contain error message", e.getMessage().contains("Failed to load top selling items"));
            verify(dashboardDAO).getTopSellingItems(5);
        }
    }

}