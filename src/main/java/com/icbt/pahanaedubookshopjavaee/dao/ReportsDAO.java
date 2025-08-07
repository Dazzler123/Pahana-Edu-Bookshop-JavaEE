package com.icbt.pahanaedubookshopjavaee.dao;

import com.icbt.pahanaedubookshopjavaee.dto.OrderReportDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ReportFilterDTO;

import java.util.List;
import java.util.Map;

public interface ReportsDAO {
    List<OrderReportDTO> getOrderReports(ReportFilterDTO filter);
    Map<String, Object> getReportSummary(ReportFilterDTO filter);
    List<Map<String, Object>> getDailyReports(ReportFilterDTO filter);
    List<Map<String, Object>> getMonthlyReports(ReportFilterDTO filter);
    List<Map<String, Object>> getAnnualReports(ReportFilterDTO filter);
}