package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.ReportsDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.ReportsDAOImpl;
import com.icbt.pahanaedubookshopjavaee.dto.OrderReportDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ReportFilterDTO;
import com.icbt.pahanaedubookshopjavaee.service.ReportsService;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class ReportsServiceImpl implements ReportsService {
    
    private final ReportsDAO reportsDAO;

    public ReportsServiceImpl(DataSource dataSource) {
        this.reportsDAO = new ReportsDAOImpl(dataSource);
    }

    @Override
    public List<OrderReportDTO> generateOrderReports(ReportFilterDTO filter) {
        return reportsDAO.getOrderReports(filter);
    }

    @Override
    public Map<String, Object> getReportSummary(ReportFilterDTO filter) {
        return reportsDAO.getReportSummary(filter);
    }

    @Override
    public List<Map<String, Object>> generateTimeBasedReports(ReportFilterDTO filter) {
        if ("DAILY".equals(filter.getReportType())) {
            return reportsDAO.getDailyReports(filter);
        } else if ("MONTHLY".equals(filter.getReportType())) {
            return reportsDAO.getMonthlyReports(filter);
        } else if ("ANNUAL".equals(filter.getReportType())) {
            return reportsDAO.getAnnualReports(filter);
        }
        return reportsDAO.getDailyReports(filter); // default to daily
    }
}