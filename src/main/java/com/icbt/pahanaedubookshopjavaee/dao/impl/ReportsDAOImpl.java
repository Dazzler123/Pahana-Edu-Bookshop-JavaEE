package com.icbt.pahanaedubookshopjavaee.dao.impl;

import com.icbt.pahanaedubookshopjavaee.dao.ReportsDAO;
import com.icbt.pahanaedubookshopjavaee.dto.OrderReportDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ReportFilterDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ReportsDAOImpl implements ReportsDAO {
    
    private final DataSource dataSource;

    public ReportsDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<OrderReportDTO> getOrderReports(ReportFilterDTO filter) {
        StringBuilder query = new StringBuilder(
            "SELECT o.order_code, o.customer_id, c.name as customer_name, o.order_date, " +
            "o.total_amount, o.total_discount_applied, o.status, o.payment_status, " +
            "COUNT(oi.item_id) as item_count " +
            "FROM Orders o " +
            "JOIN Customer c ON o.customer_id = c.account_number " +
            "LEFT JOIN Order_Item oi ON o.order_code = oi.order_id " +
            "WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();
        buildWhereClause(query, params, filter);
        
        query.append(" GROUP BY o.order_code, o.customer_id, c.name, o.order_date, " +
                    "o.total_amount, o.total_discount_applied, o.status, o.payment_status " +
                    "ORDER BY o.order_date DESC");

        List<OrderReportDTO> reports = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {
            
            setParameters(ps, params);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(new OrderReportDTO(
                        rs.getString("order_code"),
                        rs.getString("customer_id"),
                        rs.getString("customer_name"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getBigDecimal("total_amount"),
                        rs.getBigDecimal("total_discount_applied"),
                        rs.getString("status"),
                        rs.getString("payment_status"),
                        rs.getInt("item_count")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to generate order reports", e);
        }

        return reports;
    }

    @Override
    public Map<String, Object> getReportSummary(ReportFilterDTO filter) {
        StringBuilder query = new StringBuilder(
            "SELECT COUNT(*) as total_orders, " +
            "COALESCE(SUM(total_amount), 0) as total_revenue, " +
            "COALESCE(SUM(total_discount_applied), 0) as total_discounts, " +
            "COALESCE(AVG(total_amount), 0) as avg_order_value " +
            "FROM Orders o WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();
        buildWhereClauseForSummary(query, params, filter);

        Map<String, Object> summary = new HashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {
            
            setParameters(ps, params);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.put("totalOrders", rs.getInt("total_orders"));
                    summary.put("totalRevenue", rs.getBigDecimal("total_revenue"));
                    summary.put("totalDiscounts", rs.getBigDecimal("total_discounts"));
                    summary.put("avgOrderValue", rs.getBigDecimal("avg_order_value"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to generate report summary", e);
        }

        return summary;
    }

    @Override
    public List<Map<String, Object>> getDailyReports(ReportFilterDTO filter) {
        StringBuilder query = new StringBuilder(
            "SELECT DATE(order_date) as report_date, " +
            "COUNT(*) as order_count, " +
            "COALESCE(SUM(total_amount), 0) as daily_revenue " +
            "FROM Orders WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();
        buildWhereClauseForSummary(query, params, filter);
        query.append(" GROUP BY DATE(order_date) ORDER BY report_date DESC");

        return executeTimeBasedReport(query.toString(), params);
    }

    @Override
    public List<Map<String, Object>> getMonthlyReports(ReportFilterDTO filter) {
        StringBuilder query = new StringBuilder(
            "SELECT YEAR(order_date) as report_year, MONTH(order_date) as report_month, " +
            "COUNT(*) as order_count, " +
            "COALESCE(SUM(total_amount), 0) as monthly_revenue " +
            "FROM Orders WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();
        buildWhereClauseForSummary(query, params, filter);
        query.append(" GROUP BY YEAR(order_date), MONTH(order_date) ORDER BY report_year DESC, report_month DESC");

        return executeTimeBasedReport(query.toString(), params);
    }

    @Override
    public List<Map<String, Object>> getAnnualReports(ReportFilterDTO filter) {
        StringBuilder query = new StringBuilder(
            "SELECT YEAR(order_date) as report_year, " +
            "COUNT(*) as order_count, " +
            "COALESCE(SUM(total_amount), 0) as annual_revenue " +
            "FROM Orders WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();
        buildWhereClauseForSummary(query, params, filter);
        query.append(" GROUP BY YEAR(order_date) ORDER BY report_year DESC");

        return executeTimeBasedReport(query.toString(), params);
    }

    private void buildWhereClause(StringBuilder query, List<Object> params, ReportFilterDTO filter) {
        if (filter.getCustomerId() != null && !filter.getCustomerId().trim().isEmpty()) {
            query.append(" AND o.customer_id = ?");
            params.add(filter.getCustomerId());
        }
        
        if (filter.getOrderId() != null && !filter.getOrderId().trim().isEmpty()) {
            query.append(" AND o.order_code LIKE ?");
            params.add("%" + filter.getOrderId() + "%");
        }
        
        if (filter.getItemCode() != null && !filter.getItemCode().trim().isEmpty()) {
            query.append(" AND EXISTS (SELECT 1 FROM Order_Item oi2 WHERE oi2.order_id = o.order_code AND oi2.item_id = ?)");
            params.add(filter.getItemCode());
        }
        
        if (filter.getStartDate() != null) {
            query.append(" AND DATE(o.order_date) >= ?");
            params.add(Date.valueOf(filter.getStartDate()));
        }
        
        if (filter.getEndDate() != null) {
            query.append(" AND DATE(o.order_date) <= ?");
            params.add(Date.valueOf(filter.getEndDate()));
        }
        
        if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
            query.append(" AND o.status = ?");
            params.add(filter.getStatus());
        }
        
        if (filter.getPaymentStatus() != null && !filter.getPaymentStatus().trim().isEmpty()) {
            query.append(" AND o.payment_status = ?");
            params.add(filter.getPaymentStatus());
        }
    }

    private void buildWhereClauseForSummary(StringBuilder query, List<Object> params, ReportFilterDTO filter) {
        if (filter.getCustomerId() != null && !filter.getCustomerId().trim().isEmpty()) {
            query.append(" AND customer_id = ?");
            params.add(filter.getCustomerId());
        }
        
        if (filter.getOrderId() != null && !filter.getOrderId().trim().isEmpty()) {
            query.append(" AND order_code LIKE ?");
            params.add("%" + filter.getOrderId() + "%");
        }
        
        if (filter.getStartDate() != null) {
            query.append(" AND DATE(order_date) >= ?");
            params.add(Date.valueOf(filter.getStartDate()));
        }
        
        if (filter.getEndDate() != null) {
            query.append(" AND DATE(order_date) <= ?");
            params.add(Date.valueOf(filter.getEndDate()));
        }
        
        if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
            query.append(" AND status = ?");
            params.add(filter.getStatus());
        }
        
        if (filter.getPaymentStatus() != null && !filter.getPaymentStatus().trim().isEmpty()) {
            query.append(" AND payment_status = ?");
            params.add(filter.getPaymentStatus());
        }
    }

    private List<Map<String, Object>> executeTimeBasedReport(String query, List<Object> params) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            setParameters(ps, params);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute time-based report", e);
        }

        return results;
    }

    private void setParameters(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}