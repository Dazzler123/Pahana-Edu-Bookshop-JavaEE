package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.PlaceOrderDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.PlaceOrderDAOImpl;
import com.icbt.pahanaedubookshopjavaee.service.PlaceOrderService;

import javax.sql.DataSource;

public class PlaceOrderServiceImpl implements PlaceOrderService {

    private final PlaceOrderDAO placeOrderDAO;

    public PlaceOrderServiceImpl(DataSource dataSource) {
        this.placeOrderDAO = new PlaceOrderDAOImpl(dataSource);
    }


}
