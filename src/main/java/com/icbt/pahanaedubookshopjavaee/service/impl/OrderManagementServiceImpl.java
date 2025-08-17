package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.OrderManagementDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.OrderManagementDAOImpl;
import com.icbt.pahanaedubookshopjavaee.service.OrderManagementService;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;
import com.icbt.pahanaedubookshopjavaee.util.constants.ExceptionMessages;

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
                        .add("message", ResponseMessages.MESSAGE_CUSTOMER_ACCOUNT_REQUIRED)
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
                    .add("message", ResponseMessages.MESSAGE_FAILED_TO_LOAD_ORDERS + ": " + e.getMessage())
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
                        .add("message", ResponseMessages.MESSAGE_ORDER_CODE_REQUIRED)
                        .build();
            }

            if (!orderRequest.containsKey("orderDate") || orderRequest.getString("orderDate").trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_ORDER_DATE_REQUIRED)
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
                            .add("message", ResponseMessages.MESSAGE_TOTAL_AMOUNT_REQUIRED)
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
                            .add("message", ResponseMessages.MESSAGE_TOTAL_DISCOUNT_REQUIRED)
                            .build();
                }
            } catch (NumberFormatException e) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_INVALID_NUMERIC_VALUE_IN_ORDER)
                        .build();
            }

            // Validate numeric values
            if (totalAmount < 0) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_TOTAL_AMOUNT_CANNOT_BE_NEGATIVE)
                        .build();
            }

            if (totalDiscount < 0) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_TOTAL_DISCOUNT_CANNOT_BE_NEGATIVE)
                        .build();
            }

            if (totalDiscount > totalAmount) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_DISCOUNT_CANNOT_EXCEED_TOTAL)
                        .build();
            }

            // Validate date format
            try {
                LocalDateTime.parse(orderDate);
            } catch (DateTimeParseException e) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_INVALID_ORDER_DATE_FORMAT)
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
                        .add("message", ResponseMessages.MESSAGE_INVALID_ORDER_STATUS)
                        .build();
            }

            if (!paymentStatus.matches("[ANP]")) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_INVALID_PAYMENT_STATUS)
                        .build();
            }

            // Validate payment type
            if (!isValidPaymentType(paymentType)) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_INVALID_PAYMENT_TYPE)
                        .build();
            }

            // Update the order
            updateOrder(orderCode, orderDate, totalAmount, totalDiscount, status, paymentStatus, paymentType);

            return Json.createObjectBuilder()
                    .add("state", "success")
                    .add("message", ResponseMessages.MESSAGE_ORDER_UPDATED_SUCCESSFULLY)
                    .build();
                    
        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", ExceptionMessages.FAILED_TO_UPDATE_ORDER + ": " + e.getMessage())
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
                        .add("message", ResponseMessages.MESSAGE_ORDER_CODE_REQUIRED)
                        .build();
            }

            if (!statusRequest.containsKey("status") || statusRequest.getString("status").trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_STATUS_REQUIRED)
                        .build();
            }

            String orderCode = statusRequest.getString("orderCode");
            String status = statusRequest.getString("status");

            // Validate status
            if (!status.matches("[AID]")) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_INVALID_ORDER_STATUS)
                        .build();
            }

            // Update order status
            updateOrderStatus(orderCode, status);

            // Generate appropriate success message
            String actionText = status.equals("A") ? "activated" :
                    status.equals("I") ? "inactivated" : "deleted";

            return Json.createObjectBuilder()
                    .add("state", "success")
                    .add("message", ResponseMessages.MESSAGE_ORDER_STATUS_UPDATED_SUCCESSFULLY.replace(CommonConstants.REPLACER, actionText))
                    .build();
                    
        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", ExceptionMessages.FAILED_TO_UPDATE_ORDER_STATUS + ": " + e.getMessage())
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
