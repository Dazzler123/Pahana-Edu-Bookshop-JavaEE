package com.icbt.pahanaedubookshopjavaee.service;

import javax.json.JsonObject;

public interface OrderManagementService {
    JsonObject getOrdersByCustomer(String customerId) throws Exception;

    void updateOrder(String orderCode, String orderDate, double totalAmount, double totalDiscount, String status, String paymentStatus) throws Exception;

    void updateOrderStatus(String orderCode, String status) throws Exception;
}