package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.dto.ReportFilterDTO;
import com.icbt.pahanaedubookshopjavaee.service.ReportsService;

import javax.json.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/reports")
public class ReportsServlet extends BaseServlet {

    private ReportsService reportsService;

    @Override
    protected void initializeServices() {
        this.reportsService = serviceFactory.createReportsService();
    }

    /**
     * This method is used to generate reports
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reportType = request.getParameter("reportType");
        ReportFilterDTO filter = buildFilterFromRequest(request);

        JsonObject result;

        if ("summary".equals(reportType)) {
            result = reportsService.processSummaryReportRequest(filter);
        } else if ("time-based".equals(reportType)) {
            result = reportsService.processTimeBasedReportRequest(filter);
        } else {
            result = reportsService.processDetailedReportRequest(filter);
        }

        if (result.containsKey("state") && "error".equals(result.getString("state"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        abstractResponseUtility.writeJson(response, result);
    }

    /**
     * This method is used to build the filter from the request
     *
     * @param request
     * @return
     */
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
            try {
                filter.setStartDate(LocalDate.parse(startDate));
            } catch (Exception e) {
                // Invalid date format will be handled by service validation
            }
        }

        String endDate = request.getParameter("endDate");
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                filter.setEndDate(LocalDate.parse(endDate));
            } catch (Exception e) {
                // Invalid date format will be handled by service validation
            }
        }

        return filter;
    }

}