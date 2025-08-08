package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.PlaceOrderDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.PlaceOrderDAOImpl;
import com.icbt.pahanaedubookshopjavaee.dto.PlaceOrderDTO;
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
    public String placeOrder(PlaceOrderDTO placeOrderDTO) throws Exception {
        return placeOrderDAO.createOrder(
            placeOrderDTO.getCustomerId(),
            placeOrderDTO.getTotalAmount(),
            placeOrderDTO.getTotalDiscount(),
            placeOrderDTO.getOrderItems(),
            placeOrderDTO.getPaymentStatus(),
            placeOrderDTO.getPaymentMethod()
        );
    }

}
