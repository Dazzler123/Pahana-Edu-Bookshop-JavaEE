package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.service.OrderManagementService;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class OrderManagementServiceImpl implements OrderManagementService {
    
    private final DataSource dataSource;

    public OrderManagementServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public JsonObject getOrdersByCustomer(String customerId) throws Exception {
        String query = "SELECT order_code, customer_id, total_amount, total_discount_applied, " +
                      "order_date, status, payment_status, payment_method FROM Orders WHERE customer_id = ? ORDER BY order_date DESC";
        
        JsonArrayBuilder ordersArray = Json.createArrayBuilder();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, customerId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordersArray.add(Json.createObjectBuilder()
                            .add("orderCode", rs.getString("order_code"))
                            .add("customerId", rs.getString("customer_id"))
                            .add("totalAmount", rs.getBigDecimal("total_amount"))
                            .add("totalDiscount", rs.getBigDecimal("total_discount_applied"))
                            .add("orderDate", rs.getTimestamp("order_date").toString())
                            .add("status", rs.getString("status"))
                            .add("paymentStatus", rs.getString("payment_status"))
                            .add("paymentType", rs.getString("payment_method") != null ? rs.getString("payment_method") : "cash")
                    );
                }
            }
        }
        
        return Json.createObjectBuilder()
                .add("orders", ordersArray)
                .build();
    }

    @Override
    public void updateOrder(String orderCode, String orderDate, double totalAmount, double totalDiscount, String status, String paymentStatus, String paymentType) throws Exception {
        String query = "UPDATE Orders SET total_amount = ?, total_discount_applied = ?, " +
                      "order_date = ?, status = ?, payment_status = ?, payment_method = ? WHERE order_code = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(totalAmount));
            ps.setBigDecimal(2, java.math.BigDecimal.valueOf(totalDiscount));
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.parse(orderDate)));
            ps.setString(4, status);
            ps.setString(5, paymentStatus);
            ps.setString(6, paymentType);
            ps.setString(7, orderCode);
            
            ps.executeUpdate();
        }
    }

    @Override
    public void updateOrderStatus(String orderCode, String status) throws Exception {
        String query = "UPDATE Orders SET status = ? WHERE order_code = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, status);
            ps.setString(2, orderCode);
            
            ps.executeUpdate();
        }
    }
}