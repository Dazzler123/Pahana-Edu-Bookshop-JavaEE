package com.icbt.pahanaedubookshopjavaee.dao;

import com.icbt.pahanaedubookshopjavaee.dto.OrderReportDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ReportFilterDTO;

import javax.json.JsonObject;
import java.util.List;
import java.util.Map;

public interface ReportsDAO {
    List<OrderReportDTO> getOrderReports(ReportFilterDTO filter) throws Exception;

    Map<String, Object> getReportSummary(ReportFilterDTO filter) throws Exception;

    List<Map<String, Object>> getDailyReports(ReportFilterDTO filter) throws Exception;

    List<Map<String, Object>> getMonthlyReports(ReportFilterDTO filter) throws Exception;

    List<Map<String, Object>> getAnnualReports(ReportFilterDTO filter) throws Exception;

    JsonObject generateDetailedReportData(ReportFilterDTO filter) throws Exception;

    JsonObject generateSummaryReportData(ReportFilterDTO filter) throws Exception;

    JsonObject generateTimeBasedReportData(ReportFilterDTO filter) throws Exception;
}
