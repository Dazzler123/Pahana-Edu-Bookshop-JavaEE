package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.ReportsDAO;
import com.icbt.pahanaedubookshopjavaee.dto.OrderReportDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ReportFilterDTO;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportsServiceImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private ReportsDAO reportsDAO;

    private ReportsServiceImpl reportsService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reportsService = new ReportsServiceImpl(dataSource);

        try {
            java.lang.reflect.Field daoField = ReportsServiceImpl.class.getDeclaredField("reportsDAO");
            daoField.setAccessible(true);
            daoField.set(reportsService, reportsDAO);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testGenerateOrderReports_Success() throws Exception {
        // Arrange
        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setCustomerId("CUS-001");

        List<OrderReportDTO> mockReports = Arrays.asList(
            new OrderReportDTO("ORD-001", "CUS-001", "John Doe", 
                LocalDateTime.now(), new BigDecimal("100.00"), 
                new BigDecimal("10.00"), "COMPLETED", "PAID", 2)
        );
        when(reportsDAO.getOrderReports(filter)).thenReturn(mockReports);

        // Act
        List<OrderReportDTO> result = reportsService.generateOrderReports(filter);

        // Assert
        assertEquals(1, result.size());
        assertEquals("ORD-001", result.get(0).getOrderCode());
        verify(reportsDAO).getOrderReports(filter);
    }

    @Test
    public void testGenerateOrderReports_Exception() throws Exception {
        // Arrange
        ReportFilterDTO filter = new ReportFilterDTO();
        when(reportsDAO.getOrderReports(filter)).thenThrow(new Exception("Database error"));

        // Act & Assert
        try {
            reportsService.generateOrderReports(filter);
            fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            assertEquals("Failed to generate order reports", e.getMessage());
        }
    }

    @Test
    public void testGenerateTimeBasedReports_Daily() throws Exception {
        // Arrange
        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setReportType(CommonConstants.REPORT_FILTER_DAILY);

        List<Map<String, Object>> mockReports = Arrays.asList(
            createTimeReportMap("2024-01-01", 5, "500.00")
        );
        when(reportsDAO.getDailyReports(filter)).thenReturn(mockReports);

        // Act
        List<Map<String, Object>> result = reportsService.generateTimeBasedReports(filter);

        // Assert
        assertEquals(1, result.size());
        verify(reportsDAO).getDailyReports(filter);
        verify(reportsDAO, never()).getMonthlyReports(any());
        verify(reportsDAO, never()).getAnnualReports(any());
    }

    @Test
    public void testGenerateTimeBasedReports_Monthly() throws Exception {
        // Arrange
        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setReportType(CommonConstants.REPORT_FILTER_MONTHLY);

        List<Map<String, Object>> mockReports = Arrays.asList(
            createTimeReportMap("2024-01", 25, "2500.00")
        );
        when(reportsDAO.getMonthlyReports(filter)).thenReturn(mockReports);

        // Act
        List<Map<String, Object>> result = reportsService.generateTimeBasedReports(filter);

        // Assert
        assertEquals(1, result.size());
        verify(reportsDAO).getMonthlyReports(filter);
        verify(reportsDAO, never()).getDailyReports(any());
        verify(reportsDAO, never()).getAnnualReports(any());
    }

    @Test
    public void testGenerateTimeBasedReports_Annual() throws Exception {
        // Arrange
        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setReportType(CommonConstants.REPORT_FILTER_ANNUALLY);

        List<Map<String, Object>> mockReports = Arrays.asList(
            createTimeReportMap("2024", 300, "30000.00")
        );
        when(reportsDAO.getAnnualReports(filter)).thenReturn(mockReports);

        // Act
        List<Map<String, Object>> result = reportsService.generateTimeBasedReports(filter);

        // Assert
        assertEquals(1, result.size());
        verify(reportsDAO).getAnnualReports(filter);
        verify(reportsDAO, never()).getDailyReports(any());
        verify(reportsDAO, never()).getMonthlyReports(any());
    }

    @Test
    public void testGenerateTimeBasedReports_DefaultToDaily() throws Exception {
        // Arrange
        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setReportType("INVALID");

        List<Map<String, Object>> mockReports = Arrays.asList(
            createTimeReportMap("2024-01-01", 5, "500.00")
        );
        when(reportsDAO.getDailyReports(filter)).thenReturn(mockReports);

        // Act
        List<Map<String, Object>> result = reportsService.generateTimeBasedReports(filter);

        // Assert
        assertEquals(1, result.size());
        verify(reportsDAO).getDailyReports(filter);
    }

    @Test
    public void testProcessDetailedReportRequest_Success() throws Exception {
        // Arrange
        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setStartDate(LocalDate.of(2024, 1, 1));
        filter.setEndDate(LocalDate.of(2024, 1, 31));

        JsonObject mockReportData = Json.createObjectBuilder()
                .add("reports", Json.createArrayBuilder().build())
                .add("summary", Json.createObjectBuilder().build())
                .build();
        when(reportsDAO.generateDetailedReportData(filter)).thenReturn(mockReportData);

        // Act
        JsonObject result = reportsService.processDetailedReportRequest(filter);

        // Assert
        assertEquals("success", result.getString("state"));
        assertEquals(ResponseMessages.MESSAGE_DETAILED_REPORT_GENERATED, result.getString("message"));
        assertTrue(result.containsKey("reports"));
        assertTrue(result.containsKey("summary"));
    }

    @Test
    public void testProcessDetailedReportRequest_InvalidDateRange() {
        // Arrange
        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setStartDate(LocalDate.of(2024, 1, 31));
        filter.setEndDate(LocalDate.of(2024, 1, 1));

        // Act
        JsonObject result = reportsService.processDetailedReportRequest(filter);

        // Assert
        assertEquals("error", result.getString("state"));
        assertEquals(ResponseMessages.MESSAGE_INVALID_DATE_RANGE, result.getString("message"));
    }

    @Test
    public void testProcessTimeBasedReportRequest_Success() throws Exception {
        // Arrange
        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setStartDate(LocalDate.of(2024, 1, 1));
        filter.setEndDate(LocalDate.of(2024, 1, 31));
        filter.setReportType("DAILY");

        JsonObject mockTimeData = Json.createObjectBuilder()
                .add("timeReports", Json.createArrayBuilder().build())
                .build();
        when(reportsDAO.generateTimeBasedReportData(filter)).thenReturn(mockTimeData);

        // Act
        JsonObject result = reportsService.processTimeBasedReportRequest(filter);

        // Assert
        assertEquals("success", result.getString("state"));
        assertEquals(ResponseMessages.MESSAGE_TIME_BASED_REPORT_GENERATED, result.getString("message"));
        assertTrue(result.containsKey("timeReports"));
    }

    @Test
    public void testProcessTimeBasedReportRequest_InvalidReportType() {
        // Arrange
        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setStartDate(LocalDate.of(2024, 1, 1));
        filter.setEndDate(LocalDate.of(2024, 1, 31));
        filter.setReportType("INVALID");

        // Act
        JsonObject result = reportsService.processTimeBasedReportRequest(filter);

        // Assert
        assertEquals("error", result.getString("state"));
        assertEquals(ResponseMessages.MESSAGE_INVALID_REPORT_TYPE, result.getString("message"));
    }

    private Map<String, Object> createTimeReportMap(String period, int count, String revenue) {
        Map<String, Object> map = new HashMap<>();
        map.put("period", period);
        map.put("order_count", count);
        map.put("revenue", revenue);
        return map;
    }

}