package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.dto.OrderReportDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ReportFilterDTO;
import com.icbt.pahanaedubookshopjavaee.service.ReportsService;
import com.icbt.pahanaedubookshopjavaee.service.impl.ReportsServiceImpl;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;
import com.icbt.pahanaedubookshopjavaee.util.constants.DBConstants;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@WebServlet("/reports")
public class ReportsServlet extends HttpServlet {

    private ReportsService reportsService;
    private AbstractResponseUtility abstractResponseUtility;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute(DBConstants.DBCP_LABEL);
        this.reportsService = new ReportsServiceImpl(dataSource);
        this.abstractResponseUtility = new AbstractResponseUtility();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reportType = request.getParameter("reportType");
        
        try {
            ReportFilterDTO filter = buildFilterFromRequest(request);
            
            if ("summary".equals(reportType)) {
                generateSummaryReport(filter, response);
            } else if ("time-based".equals(reportType)) {
                generateTimeBasedReport(filter, response);
            } else {
                generateDetailedReport(filter, response);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("error", "Failed to generate report: " + e.getMessage())
                    .build());
        }
    }

    private ReportFilterDTO buildFilterFromRequest(HttpServletRequest request) {
        ReportFilterDTO filter = new ReportFilterDTO();
        
        filter.setCustomerId(request.getParameter("customerId"));
        filter.setOrderId(request.getParameter("orderId"));
        filter.setItemCode(request.getParameter("itemCode"));
        filter.setStatus(request.getParameter("status"));
        filter.setPaymentStatus(request.getParameter("paymentStatus"));
        filter.setReportType(request.getParameter("timeType"));
        
        String startDate = request.getParameter("startDate");
        if (startDate != null && !startDate.trim().isEmpty()) {
            filter.setStartDate(LocalDate.parse(startDate));
        }
        
        String endDate = request.getParameter("endDate");
        if (endDate != null && !endDate.trim().isEmpty()) {
            filter.setEndDate(LocalDate.parse(endDate));
        }
        
        return filter;
    }

    private void generateDetailedReport(ReportFilterDTO filter, HttpServletResponse response) throws Exception {
        List<OrderReportDTO> reports = reportsService.generateOrderReports(filter);
        Map<String, Object> summary = reportsService.getReportSummary(filter);
        
        JsonArrayBuilder reportsArray = Json.createArrayBuilder();
        for (OrderReportDTO report : reports) {
            reportsArray.add(Json.createObjectBuilder()
                    .add("orderCode", report.getOrderCode())
                    .add("customerId", report.getCustomerId())
                    .add("customerName", report.getCustomerName())
                    .add("orderDate", report.getOrderDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .add("totalAmount", report.getTotalAmount())
                    .add("totalDiscount", report.getTotalDiscount())
                    .add("status", report.getStatus())
                    .add("paymentStatus", report.getPaymentStatus())
                    .add("itemCount", report.getItemCount())
            );
        }

        JsonObject json = Json.createObjectBuilder()
                .add("reports", reportsArray)
                .add("summary", Json.createObjectBuilder()
                        .add("totalOrders", (Integer) summary.get("totalOrders"))
                        .add("totalRevenue", summary.get("totalRevenue").toString())
                        .add("totalDiscounts", summary.get("totalDiscounts").toString())
                        .add("avgOrderValue", summary.get("avgOrderValue").toString())
                )
                .build();

        abstractResponseUtility.writeJson(response, json);
    }

    private void generateSummaryReport(ReportFilterDTO filter, HttpServletResponse response) throws Exception {
        Map<String, Object> summary = reportsService.getReportSummary(filter);
        
        JsonObject json = Json.createObjectBuilder()
                .add("totalOrders", (Integer) summary.get("totalOrders"))
                .add("totalRevenue", summary.get("totalRevenue").toString())
                .add("totalDiscounts", summary.get("totalDiscounts").toString())
                .add("avgOrderValue", summary.get("avgOrderValue").toString())
                .build();

        abstractResponseUtility.writeJson(response, json);
    }

    private void generateTimeBasedReport(ReportFilterDTO filter, HttpServletResponse response) throws Exception {
        List<Map<String, Object>> timeReports = reportsService.generateTimeBasedReports(filter);
        
        JsonArrayBuilder reportsArray = Json.createArrayBuilder();
        for (Map<String, Object> report : timeReports) {
            JsonObjectBuilder reportBuilder = Json.createObjectBuilder();
            for (Map.Entry<String, Object> entry : report.entrySet()) {
                if (entry.getValue() != null) {
                    reportBuilder.add(entry.getKey(), entry.getValue().toString());
                }
            }
            reportsArray.add(reportBuilder);
        }

        JsonObject json = Json.createObjectBuilder()
                .add("timeReports", reportsArray)
                .build();

        abstractResponseUtility.writeJson(response, json);
    }
}