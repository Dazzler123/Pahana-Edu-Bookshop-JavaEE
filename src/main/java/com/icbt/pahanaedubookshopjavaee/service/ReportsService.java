package com.icbt.pahanaedubookshopjavaee.service;

import com.icbt.pahanaedubookshopjavaee.dto.OrderReportDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ReportFilterDTO;

import java.util.List;
import java.util.Map;

public interface ReportsService {
    List<OrderReportDTO> generateOrderReports(ReportFilterDTO filter);
    Map<String, Object> getReportSummary(ReportFilterDTO filter);
    List<Map<String, Object>> generateTimeBasedReports(ReportFilterDTO filter);
}