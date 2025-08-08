package com.icbt.pahanaedubookshopjavaee.service;

import com.icbt.pahanaedubookshopjavaee.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface PlaceOrderService {
    String placeOrder(String customerId, BigDecimal totalAmount, BigDecimal totalDiscount,
                      List<OrderItem> orderItems, String paymentStatus) throws Exception;
}

