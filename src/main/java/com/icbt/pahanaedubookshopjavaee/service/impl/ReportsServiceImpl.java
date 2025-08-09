package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.ReportsDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.ReportsDAOImpl;
import com.icbt.pahanaedubookshopjavaee.dto.OrderReportDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ReportFilterDTO;
import com.icbt.pahanaedubookshopjavaee.service.ReportsService;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;

import javax.json.Json;
import javax.json.JsonObject;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class ReportsServiceImpl implements ReportsService {

    private final ReportsDAO reportsDAO;

    public ReportsServiceImpl(DataSource dataSource) {
        this.reportsDAO = new ReportsDAOImpl(dataSource);
    }

    /**
     * This method is used to generate the order reports
     *
     * @param filter
     * @return
     */
    @Override
    public List<OrderReportDTO> generateOrderReports(ReportFilterDTO filter) {
        try {
            return reportsDAO.getOrderReports(filter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate order reports", e);
        }
    }

    /**
     * This method is used to generate the report summary
     *
     * @param filter
     * @return
     */
    @Override
    public Map<String, Object> getReportSummary(ReportFilterDTO filter) {
        try {
            return reportsDAO.getReportSummary(filter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get report summary", e);
        }
    }

    /**
     * This method is used to generate the time-based reports
     *
     * @param filter
     * @return
     */
    @Override
    public List<Map<String, Object>> generateTimeBasedReports(ReportFilterDTO filter) {
        try {
            if ("DAILY".equals(filter.getReportType())) {
                return reportsDAO.getDailyReports(filter);
            } else if ("MONTHLY".equals(filter.getReportType())) {
                return reportsDAO.getMonthlyReports(filter);
            } else if ("ANNUAL".equals(filter.getReportType())) {
                return reportsDAO.getAnnualReports(filter);
            }
            return reportsDAO.getDailyReports(filter); // default to daily
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate time-based reports", e);
        }
    }

    /**
     * This method is used to process the detailed report request
     *
     * @param filter
     * @return
     */
    @Override
    public JsonObject processDetailedReportRequest(ReportFilterDTO filter) {
        try {
            // Validate filter
            JsonObject validationResult = validateReportFilter(filter);
            if (validationResult != null) {
                return validationResult;
            }

            JsonObject reportData = reportsDAO.generateDetailedReportData(filter);

            return Json.createObjectBuilder()
                    .add("state", "success")
                    .add("reports", reportData.getJsonArray("reports"))
                    .add("summary", reportData.getJsonObject("summary"))
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", "Failed to generate detailed report: " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to process the summary report request
     *
     * @param filter
     * @return
     */
    @Override
    public JsonObject processSummaryReportRequest(ReportFilterDTO filter) {
        try {
            // Validate filter
            JsonObject validationResult = validateReportFilter(filter);
            if (validationResult != null) {
                return validationResult;
            }

            JsonObject summaryData = reportsDAO.generateSummaryReportData(filter);

            return Json.createObjectBuilder()
                    .add("state", "success")
                    .add("totalOrders", summaryData.getInt("totalOrders"))
                    .add("totalRevenue", summaryData.getString("totalRevenue"))
                    .add("totalDiscounts", summaryData.getString("totalDiscounts"))
                    .add("avgOrderValue", summaryData.getString("avgOrderValue"))
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", "Failed to generate summary report: " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to process the time-based report request
     *
     * @param filter
     * @return
     */
    @Override
    public JsonObject processTimeBasedReportRequest(ReportFilterDTO filter) {
        try {
            // Validate filter
            JsonObject validationResult = validateReportFilter(filter);
            if (validationResult != null) {
                return validationResult;
            }

            // Validate report type
            if (filter.getReportType() == null || filter.getReportType().trim().isEmpty()) {
                filter.setReportType("DAILY"); // default
            }

            if (!isValidReportType(filter.getReportType())) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Invalid report type. Must be DAILY, MONTHLY, or ANNUAL")
                        .build();
            }

            JsonObject timeData = reportsDAO.generateTimeBasedReportData(filter);

            return Json.createObjectBuilder()
                    .add("state", "success")
                    .add("timeReports", timeData.getJsonArray("timeReports"))
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", "Failed to generate time-based report: " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to validate the report filter
     *
     * @param filter
     * @return
     */
    private JsonObject validateReportFilter(ReportFilterDTO filter) {
        if (filter == null) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", "Report filter is required")
                    .build();
        }

        // Validate date range
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            if (filter.getStartDate().isAfter(filter.getEndDate())) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Start date cannot be after end date")
                        .build();
            }
        }

        // Validate date range is not too large (e.g., more than 2 years)
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(filter.getStartDate(), filter.getEndDate());
            if (daysBetween > 730) { // 2 years
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Date range cannot exceed 2 years")
                        .build();
            }
        }

        // Validate status values
        if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
            if (!filter.getStatus().matches("[AID]")) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Invalid order status. Must be A, I, or D")
                        .build();
            }
        }

        // Validate payment status values
        if (filter.getPaymentStatus() != null && !filter.getPaymentStatus().trim().isEmpty()) {
            if (!filter.getPaymentStatus().matches("[PNR]")) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Invalid payment status. Must be P, N, or R")
                        .build();
            }
        }

        return null; // No validation errors
    }

    /**
     * This method is used to validate the report type
     *
     * @param reportType
     * @return
     */
    private boolean isValidReportType(String reportType) {
        return CommonConstants.REPORT_FILTER_DAILY.equals(reportType) ||
                CommonConstants.REPORT_FILTER_MONTHLY.equals(reportType) ||
                CommonConstants.REPORT_FILTER_ANNUALLY.equals(reportType);
    }

}
