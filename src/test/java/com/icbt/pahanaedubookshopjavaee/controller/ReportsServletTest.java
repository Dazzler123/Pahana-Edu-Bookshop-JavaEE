package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.factory.ServiceFactory;
import com.icbt.pahanaedubookshopjavaee.service.ReportsService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportsServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServiceFactory serviceFactory;

    @Mock
    private ReportsService reportsService;

    @Mock
    private AbstractResponseUtility responseUtility;

    private ReportsServlet reportsServlet;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reportsServlet = new ReportsServlet();

        try {
            java.lang.reflect.Field serviceFactoryField = ReportsServlet.class.getSuperclass().getDeclaredField("serviceFactory");
            serviceFactoryField.setAccessible(true);
            serviceFactoryField.set(reportsServlet, serviceFactory);

            java.lang.reflect.Field responseUtilityField = ReportsServlet.class.getSuperclass().getDeclaredField("abstractResponseUtility");
            responseUtilityField.setAccessible(true);
            responseUtilityField.set(reportsServlet, responseUtility);

            java.lang.reflect.Field reportsServiceField = ReportsServlet.class.getDeclaredField("reportsService");
            reportsServiceField.setAccessible(true);
            reportsServiceField.set(reportsServlet, reportsService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testDoGet_DetailedReport_ShouldReturnDetailedReport() throws IOException {
        // Arrange
        when(request.getParameter("reportType")).thenReturn("detailed");
        when(request.getParameter("customerId")).thenReturn("CUS-001");
        when(request.getParameter("startDate")).thenReturn("2024-01-01");
        when(request.getParameter("endDate")).thenReturn("2024-01-31");

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "success")
                .add("message", "Detailed report generated successfully")
                .add("reports", Json.createArrayBuilder().build())
                .add("summary", Json.createObjectBuilder().build())
                .build();
        when(reportsService.processDetailedReportRequest(any())).thenReturn(mockResponse);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportsService).processDetailedReportRequest(any());
        verify(responseUtility).writeJson(response, mockResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoGet_SummaryReport_ShouldReturnSummaryReport() throws IOException {
        // Arrange
        when(request.getParameter("reportType")).thenReturn("summary");
        when(request.getParameter("startDate")).thenReturn("2024-01-01");
        when(request.getParameter("endDate")).thenReturn("2024-01-31");

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "success")
                .add("message", "Summary report generated successfully")
                .add("totalOrders", 25)
                .add("totalRevenue", "1250.50")
                .add("totalDiscounts", "125.00")
                .add("avgOrderValue", "50.02")
                .build();
        when(reportsService.processSummaryReportRequest(any())).thenReturn(mockResponse);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportsService).processSummaryReportRequest(any());
        verify(responseUtility).writeJson(response, mockResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoGet_TimeBasedReport_ShouldReturnTimeBasedReport() throws IOException {
        // Arrange
        when(request.getParameter("reportType")).thenReturn("time-based");
        when(request.getParameter("timeType")).thenReturn("MONTHLY");
        when(request.getParameter("startDate")).thenReturn("2024-01-01");
        when(request.getParameter("endDate")).thenReturn("2024-12-31");

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "success")
                .add("message", "Time-based report generated successfully")
                .add("timeReports", Json.createArrayBuilder().build())
                .build();
        when(reportsService.processTimeBasedReportRequest(any())).thenReturn(mockResponse);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportsService).processTimeBasedReportRequest(any());
        verify(responseUtility).writeJson(response, mockResponse);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoGet_DefaultToDetailedReport_WhenNoReportType() throws IOException {
        // Arrange
        when(request.getParameter("reportType")).thenReturn(null);
        when(request.getParameter("customerId")).thenReturn("CUS-001");

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "success")
                .add("message", "Detailed report generated successfully")
                .add("reports", Json.createArrayBuilder().build())
                .add("summary", Json.createObjectBuilder().build())
                .build();
        when(reportsService.processDetailedReportRequest(any())).thenReturn(mockResponse);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportsService).processDetailedReportRequest(any());
        verify(responseUtility).writeJson(response, mockResponse);
    }

    @Test
    public void testDoGet_ErrorResponse_ShouldSetBadRequestStatus() throws IOException {
        // Arrange
        when(request.getParameter("reportType")).thenReturn("summary");
        when(request.getParameter("startDate")).thenReturn("2024-01-31");
        when(request.getParameter("endDate")).thenReturn("2024-01-01");

        JsonObject errorResponse = Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "Invalid date range. Start date must be before end date")
                .build();
        when(reportsService.processSummaryReportRequest(any())).thenReturn(errorResponse);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseUtility).writeJson(response, errorResponse);
    }

    @Test
    public void testDoGet_WithAllFilters_ShouldPassAllParameters() throws IOException {
        // Arrange
        when(request.getParameter("reportType")).thenReturn("detailed");
        when(request.getParameter("customerId")).thenReturn("CUS-001");
        when(request.getParameter("orderId")).thenReturn("ORD-001");
        when(request.getParameter("itemCode")).thenReturn("ITM-001");
        when(request.getParameter("status")).thenReturn("COMPLETED");
        when(request.getParameter("paymentStatus")).thenReturn("PAID");
        when(request.getParameter("startDate")).thenReturn("2024-01-01");
        when(request.getParameter("endDate")).thenReturn("2024-01-31");
        when(request.getParameter("timeType")).thenReturn("DAILY");

        JsonObject mockResponse = Json.createObjectBuilder()
                .add("state", "success")
                .add("message", "Detailed report generated successfully")
                .add("reports", Json.createArrayBuilder().build())
                .add("summary", Json.createObjectBuilder().build())
                .build();
        when(reportsService.processDetailedReportRequest(any())).thenReturn(mockResponse);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportsService).processDetailedReportRequest(argThat(filter -> 
            "CUS-001".equals(filter.getCustomerId()) &&
            "ORD-001".equals(filter.getOrderId()) &&
            "ITM-001".equals(filter.getItemCode()) &&
            "COMPLETED".equals(filter.getStatus()) &&
            "PAID".equals(filter.getPaymentStatus()) &&
            "DAILY".equals(filter.getReportType())
        ));
        verify(responseUtility).writeJson(response, mockResponse);
    }

}
