package com.icbt.pahanaedubookshopjavaee.dao.impl;

import com.icbt.pahanaedubookshopjavaee.dao.PlaceOrderDAO;

import javax.sql.DataSource;

public class PlaceOrderDAOImpl implements PlaceOrderDAO {

    private final DataSource dataSource;

    public PlaceOrderDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
