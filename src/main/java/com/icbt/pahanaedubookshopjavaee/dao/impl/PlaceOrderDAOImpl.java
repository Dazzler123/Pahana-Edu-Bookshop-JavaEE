package com.icbt.pahanaedubookshopjavaee.dao.impl;

import com.icbt.pahanaedubookshopjavaee.dao.PlaceOrderDAO;
import com.icbt.pahanaedubookshopjavaee.model.OrderItem;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ExceptionMessages;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlaceOrderDAOImpl implements PlaceOrderDAO {

    private final DataSource dataSource;

    public PlaceOrderDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This method is used to create/place new order
     *
     * @param customerId
     * @param totalAmount
     * @param totalDiscount
     * @param orderItems
     * @return
     * @throws Exception
     */
    @Override
    public String createOrder(String customerId, BigDecimal totalAmount, BigDecimal totalDiscount, List<OrderItem> orderItems, String paymentStatus, String paymentMethod) throws Exception {
        String orderCode = generateOrderCode();
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            // insert into orders table
            String insertOrderSQL = "INSERT INTO Orders(order_code, customer_id, total_amount, total_discount_applied, " +
                    "order_date, status, payment_status, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(insertOrderSQL)) {
                ps.setString(1, orderCode);
                ps.setString(2, customerId);
                ps.setBigDecimal(3, totalAmount);
                ps.setBigDecimal(4, totalDiscount);
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(6, CommonConstants.STATUS_ACTIVE_STRING);
                ps.setString(7, paymentStatus);
                ps.setString(8, paymentMethod); // Add payment method parameter
                ps.executeUpdate();
            }

            // Insert each order item and update stock
            String insertItemSQL = "INSERT INTO Order_Item(order_id, item_id, qty, unit_price, line_total, discount_applied) VALUES (?, ?, ?, ?, ?, ?)";
            String updateStockSQL = "UPDATE Item SET qty_on_hand = qty_on_hand - ? WHERE item_code = ? AND qty_on_hand >= ?";
            try (PreparedStatement psItem = connection.prepareStatement(insertItemSQL);
                 PreparedStatement psStock = connection.prepareStatement(updateStockSQL)) {

                for (OrderItem item : orderItems) {
                    // Check stock and update
                    psStock.setInt(1, item.getQty());
                    psStock.setString(2, item.getItemCode());
                    psStock.setInt(3, item.getQty());
                    int updatedRows = psStock.executeUpdate();

                    if (updatedRows == 0) {
                        throw new Exception(ResponseMessages.MESSAGE_INSUFFICIENT_STOCK.replace(CommonConstants.REPLACER, item.getItemCode()));
                    }

                    // Insert order item
                    psItem.setString(1, orderCode);
                    psItem.setString(2, item.getItemCode());
                    psItem.setInt(3, item.getQty());
                    psItem.setBigDecimal(4, item.getUnitPrice());
                    psItem.setBigDecimal(5, item.getLineTotal());
                    psItem.setBigDecimal(6, item.getDiscountApplied());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            connection.commit();
            return orderCode;

        } catch (Exception e) {
            if (connection != null) connection.rollback();
            throw new Exception(ExceptionMessages.FAILED_TO_CREATE_ORDER + ": " + e.getMessage(), e);
        } finally {
            if (connection != null) connection.setAutoCommit(true);
            if (connection != null) connection.close();
        }
    }

    /**
     * This method can be used to create a unique order code identifier
     *
     * @return
     */
    private String generateOrderCode() {
        String prefix = "ORD-";
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        return prefix + timestamp; // e.g., ORD-20250729103045
    }

}
