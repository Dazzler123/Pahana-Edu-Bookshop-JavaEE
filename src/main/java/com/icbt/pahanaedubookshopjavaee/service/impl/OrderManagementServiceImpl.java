package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.OrderManagementDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.OrderManagementDAOImpl;
import com.icbt.pahanaedubookshopjavaee.service.OrderManagementService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class OrderManagementServiceImpl implements OrderManagementService {
    
    private final OrderManagementDAO orderManagementDAO;

    public OrderManagementServiceImpl(DataSource dataSource) {
        this.orderManagementDAO = new OrderManagementDAOImpl(dataSource);
    }

    /**
     * This method is used to get all the orders for a customer
     *
     * @param customerId
     * @return
     * @throws Exception
     */
    @Override
    public JsonObject getOrdersByCustomer(String customerId) throws Exception {
        return orderManagementDAO.getOrdersByCustomer(customerId);
    }

    /**
     * This method is used to update an order
     *
     * @param orderCode
     * @param orderDate
     * @param totalAmount
     * @param totalDiscount
     * @param status
     * @param paymentStatus
     * @param paymentType
     * @throws Exception
     */
    @Override
    public void updateOrder(String orderCode, String orderDate, double totalAmount, double totalDiscount, String status, String paymentStatus, String paymentType) throws Exception {
        orderManagementDAO.updateOrder(orderCode, orderDate, totalAmount, totalDiscount, status, paymentStatus, paymentType);
    }

    /**
     * This method is used to update the order's status
     *
     * @param orderCode
     * @param status
     * @throws Exception
     */
    @Override
    public void updateOrderStatus(String orderCode, String status) throws Exception {
        orderManagementDAO.updateOrderStatus(orderCode, status);
    }

    /**
     * This method is used to process the get orders request
     *
     * @param customerId
     * @return
     */
    @Override
    public JsonObject processGetOrdersRequest(String customerId) {
        try {
            // Validation
            if (customerId == null || customerId.trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Customer ID is required")
                        .build();
            }

            JsonObject ordersJson = getOrdersByCustomer(customerId);
            return Json.createObjectBuilder()
                    .add("state", "success")
                    .add("orders", ordersJson.getJsonArray("orders"))
                    .build();
                    
        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", "Failed to load orders: " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to process the update order request
     *
     * @param orderRequest
     * @return
     */
    @Override
    public JsonObject processUpdateOrderRequest(JsonObject orderRequest) {
        try {
            // Extract and validate required fields
            if (!orderRequest.containsKey("orderCode") || orderRequest.getString("orderCode").trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Order code is required")
                        .build();
            }

            if (!orderRequest.containsKey("orderDate") || orderRequest.getString("orderDate").trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Order date is required")
                        .build();
            }

            String orderCode = orderRequest.getString("orderCode");
            String orderDate = orderRequest.getString("orderDate");

            // Handle numeric values that might come as strings
            double totalAmount;
            double totalDiscount;

            try {
                if (orderRequest.containsKey("totalAmount")) {
                    try {
                        totalAmount = orderRequest.getJsonNumber("totalAmount").doubleValue();
                    } catch (ClassCastException e) {
                        totalAmount = Double.parseDouble(orderRequest.getString("totalAmount"));
                    }
                } else {
                    return Json.createObjectBuilder()
                            .add("state", "error")
                            .add("message", "Total amount is required")
                            .build();
                }

                if (orderRequest.containsKey("totalDiscount")) {
                    try {
                        totalDiscount = orderRequest.getJsonNumber("totalDiscount").doubleValue();
                    } catch (ClassCastException e) {
                        totalDiscount = Double.parseDouble(orderRequest.getString("totalDiscount"));
                    }
                } else {
                    return Json.createObjectBuilder()
                            .add("state", "error")
                            .add("message", "Total discount is required")
                            .build();
                }
            } catch (NumberFormatException e) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Invalid numeric value in order data")
                        .build();
            }

            // Validate numeric values
            if (totalAmount < 0) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Total amount cannot be negative")
                        .build();
            }

            if (totalDiscount < 0) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Total discount cannot be negative")
                        .build();
            }

            if (totalDiscount > totalAmount) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Total discount cannot exceed total amount")
                        .build();
            }

            // Validate date format
            try {
                LocalDateTime.parse(orderDate);
            } catch (DateTimeParseException e) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Invalid order date format")
                        .build();
            }

            // Extract other fields with defaults
            String status = orderRequest.getString("status", "A");
            String paymentStatus = orderRequest.getString("paymentStatus", "N");
            String paymentType = orderRequest.getString("paymentType", "cash");

            // Validate status values
            if (!status.matches("[AID]")) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Invalid order status. Must be A, I, or D")
                        .build();
            }

            if (!paymentStatus.matches("[PNR]")) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Invalid payment status. Must be P, N, or R")
                        .build();
            }

            // Validate payment type
            if (!isValidPaymentType(paymentType)) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Invalid payment type. Must be cash, card, or other")
                        .build();
            }

            // Update the order
            updateOrder(orderCode, orderDate, totalAmount, totalDiscount, status, paymentStatus, paymentType);

            return Json.createObjectBuilder()
                    .add("state", "success")
                    .add("message", "Order updated successfully")
                    .build();
                    
        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", "Failed to update order: " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to update the order's status
     *
     * @param statusRequest
     * @return
     */
    @Override
    public JsonObject processUpdateOrderStatusRequest(JsonObject statusRequest) {
        try {
            // Extract and validate required fields
            if (!statusRequest.containsKey("orderCode") || statusRequest.getString("orderCode").trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Order code is required")
                        .build();
            }

            if (!statusRequest.containsKey("status") || statusRequest.getString("status").trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Status is required")
                        .build();
            }

            String orderCode = statusRequest.getString("orderCode");
            String status = statusRequest.getString("status");

            // Validate status
            if (!status.matches("[AID]")) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Invalid status. Must be A (Active), I (Inactive), or D (Deleted)")
                        .build();
            }

            // Update order status
            updateOrderStatus(orderCode, status);

            // Generate appropriate success message
            String actionText = status.equals("A") ? "activated" :
                    status.equals("I") ? "inactivated" : "deleted";

            return Json.createObjectBuilder()
                    .add("state", "success")
                    .add("message", "Order " + actionText + " successfully")
                    .build();
                    
        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", "Failed to update order status: " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method can be used to validate the provided payment type
     *
     * @param paymentType
     * @return
     */
    private boolean isValidPaymentType(String paymentType) {
        return "cash".equalsIgnoreCase(paymentType) ||
               "card".equalsIgnoreCase(paymentType) ||
               "other".equalsIgnoreCase(paymentType);
    }

}
