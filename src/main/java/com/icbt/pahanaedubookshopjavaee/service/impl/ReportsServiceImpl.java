package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.ReportsDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.ReportsDAOImpl;
import com.icbt.pahanaedubookshopjavaee.dto.OrderReportDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ReportFilterDTO;
import com.icbt.pahanaedubookshopjavaee.service.ReportsService;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;
import com.icbt.pahanaedubookshopjavaee.util.constants.ExceptionMessages;

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
            throw new RuntimeException(ExceptionMessages.FAILED_TO_GENERATE_REPORT_SUMMARY, e);
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
            if (CommonConstants.REPORT_FILTER_DAILY.equals(filter.getReportType())) {
                return reportsDAO.getDailyReports(filter);
            } else if (CommonConstants.REPORT_FILTER_MONTHLY.equals(filter.getReportType())) {
                return reportsDAO.getMonthlyReports(filter);
            } else if (CommonConstants.REPORT_FILTER_ANNUALLY.equals(filter.getReportType())) {
                return reportsDAO.getAnnualReports(filter);
            }
            return reportsDAO.getDailyReports(filter); // default to daily
        } catch (Exception e) {
            throw new RuntimeException(ExceptionMessages.FAILED_TO_GENERATE_TIME_BASED_REPORTS, e);
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
                    .add("message", ResponseMessages.MESSAGE_DETAILED_REPORT_GENERATED)
                    .add("reports", reportData.getJsonArray("reports"))
                    .add("summary", reportData.getJsonObject("summary"))
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", ResponseMessages.MESSAGE_FAILED_TO_GENERATE_REPORT + ": " + e.getMessage())
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
                    .add("message", ResponseMessages.MESSAGE_SUMMARY_REPORT_GENERATED)
                    .add("totalOrders", summaryData.getInt("totalOrders"))
                    .add("totalRevenue", summaryData.getString("totalRevenue"))
                    .add("totalDiscounts", summaryData.getString("totalDiscounts"))
                    .add("avgOrderValue", summaryData.getString("avgOrderValue"))
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", ResponseMessages.MESSAGE_FAILED_TO_GENERATE_REPORT + ": " + e.getMessage())
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
                        .add("message", ResponseMessages.MESSAGE_INVALID_REPORT_TYPE)
                        .build();
            }

            JsonObject timeData = reportsDAO.generateTimeBasedReportData(filter);

            return Json.createObjectBuilder()
                    .add("state", "success")
                    .add("message", ResponseMessages.MESSAGE_TIME_BASED_REPORT_GENERATED)
                    .add("timeReports", timeData.getJsonArray("timeReports"))
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", ResponseMessages.MESSAGE_FAILED_TO_GENERATE_REPORT + ": " + e.getMessage())
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
        // Only validate dates for time-based reports
        if ("time-based".equals(filter.getReportType()) || 
            (filter.getStartDate() != null || filter.getEndDate() != null)) {
            
            if (filter.getStartDate() == null) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_START_DATE_REQUIRED)
                        .build();
            }

            if (filter.getEndDate() == null) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_END_DATE_REQUIRED)
                        .build();
            }

            if (filter.getStartDate().isAfter(filter.getEndDate())) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_INVALID_DATE_RANGE)
                        .build();
            }
        }

        return null;
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
