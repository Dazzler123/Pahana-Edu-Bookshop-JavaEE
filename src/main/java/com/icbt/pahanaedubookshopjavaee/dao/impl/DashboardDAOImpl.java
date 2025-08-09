package com.icbt.pahanaedubookshopjavaee.dao.impl;

import com.icbt.pahanaedubookshopjavaee.dao.DashboardDAO;
import com.icbt.pahanaedubookshopjavaee.dto.CustomerAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ItemAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.DashboardStatsDTO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAOImpl implements DashboardDAO {
    
    private final DataSource dataSource;

    public DashboardDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This method is used to get the dashboard statistics
     *
     * @return
     */
    @Override
    public DashboardStatsDTO getDashboardStats() {
        String totalOrdersQuery = "SELECT COUNT(*) FROM Orders WHERE status != 'D'";
        String pendingOrdersQuery = "SELECT COUNT(*) FROM Orders WHERE payment_status = 'P' AND status = 'A'";
        String totalRevenueQuery = "SELECT COALESCE(SUM(total_amount), 0) FROM Orders WHERE status = 'A' AND payment_status = 'A'";

        try (Connection conn = dataSource.getConnection()) {
            int totalOrders = 0;
            int pendingOrders = 0;
            double totalRevenue = 0.0;

            // Get total orders
            try (PreparedStatement ps = conn.prepareStatement(totalOrdersQuery);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalOrders = rs.getInt(1);
                }
            }

            // Get pending orders
            try (PreparedStatement ps = conn.prepareStatement(pendingOrdersQuery);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pendingOrders = rs.getInt(1);
                }
            }

            // Get total revenue
            try (PreparedStatement ps = conn.prepareStatement(totalRevenueQuery);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalRevenue = rs.getDouble(1);
                }
            }

            return new DashboardStatsDTO(totalOrders, pendingOrders, totalRevenue);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load dashboard statistics", e);
        }
    }

    /**
     * This method is used to get the most visited customers
     *
     * @param limit
     * @return
     */
    @Override
    public List<CustomerAnalyticsDTO> getMostVisitedCustomers(int limit) {
        String query = "SELECT c.account_number, c.name, COUNT(o.order_code) as order_count, " +
                      "COALESCE(SUM(o.total_amount), 0) as total_spent " +
                      "FROM Customer c " +
                      "LEFT JOIN Orders o ON c.account_number = o.customer_id AND o.status = 'A' " +
                      "WHERE c.status = 'A' " +
                      "GROUP BY c.account_number, c.name " +
                      "ORDER BY order_count DESC, total_spent DESC " +
                      "LIMIT ?";

        List<CustomerAnalyticsDTO> customers = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(new CustomerAnalyticsDTO(
                            rs.getString("account_number"),
                            rs.getString("name"),
                            rs.getInt("order_count"),
                            rs.getBigDecimal("total_spent")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load most visited customers", e);
        }

        return customers;
    }

    /**
     * This method is used to get the top selling items
     *
     * @param limit
     * @return
     */
    @Override
    public List<ItemAnalyticsDTO> getTopSellingItems(int limit) {
        String query = "SELECT i.item_code, i.name, " +
                      "COALESCE(SUM(oi.qty), 0) as total_sold, " +
                      "COALESCE(SUM(oi.line_total), 0) as total_revenue " +
                      "FROM Item i " +
                      "LEFT JOIN Order_Item oi ON i.item_code = oi.item_id " +
                      "LEFT JOIN Orders o ON oi.order_id = o.order_code AND o.status = 'A' " +
                      "WHERE i.status = 'A' " +
                      "GROUP BY i.item_code, i.name " +
                      "ORDER BY total_sold DESC, total_revenue DESC " +
                      "LIMIT ?";

        List<ItemAnalyticsDTO> items = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new ItemAnalyticsDTO(
                            rs.getString("item_code"),
                            rs.getString("name"),
                            rs.getInt("total_sold"),
                            rs.getBigDecimal("total_revenue")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load top selling items", e);
        }

        return items;
    }

}