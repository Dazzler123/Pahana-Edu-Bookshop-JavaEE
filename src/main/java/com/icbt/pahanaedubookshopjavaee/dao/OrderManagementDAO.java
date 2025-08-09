package com.icbt.pahanaedubookshopjavaee.dao;

import javax.json.JsonObject;

public interface OrderManagementDAO {
    JsonObject getOrdersByCustomer(String customerId) throws Exception;

    void updateOrder(String orderCode, String orderDate, double totalAmount, double totalDiscount, String status, String paymentStatus, String paymentType) throws Exception;

    void updateOrderStatus(String orderCode, String status) throws Exception;

    boolean orderExists(String orderCode) throws Exception;
}