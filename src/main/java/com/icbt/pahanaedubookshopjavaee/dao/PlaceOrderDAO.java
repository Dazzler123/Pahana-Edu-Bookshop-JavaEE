package com.icbt.pahanaedubookshopjavaee.dao;

import com.icbt.pahanaedubookshopjavaee.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface PlaceOrderDAO {
    String createOrder(String customerId, BigDecimal totalAmount, BigDecimal totalDiscount,
                      List<OrderItem> orderItems) throws Exception;
}
