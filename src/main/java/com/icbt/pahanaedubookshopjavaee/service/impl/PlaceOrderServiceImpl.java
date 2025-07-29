package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.PlaceOrderDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.PlaceOrderDAOImpl;
import com.icbt.pahanaedubookshopjavaee.model.OrderItem;
import com.icbt.pahanaedubookshopjavaee.service.PlaceOrderService;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

public class PlaceOrderServiceImpl implements PlaceOrderService {

    private final PlaceOrderDAO placeOrderDAO;

    public PlaceOrderServiceImpl(DataSource dataSource) {
        this.placeOrderDAO = new PlaceOrderDAOImpl(dataSource);
    }

    @Override
    public String placeOrder(String customerId, BigDecimal totalAmount, BigDecimal totalDiscount, List<OrderItem> orderItems) throws Exception {
        return placeOrderDAO.createOrder(customerId, totalAmount, totalDiscount, orderItems);
    }

}
