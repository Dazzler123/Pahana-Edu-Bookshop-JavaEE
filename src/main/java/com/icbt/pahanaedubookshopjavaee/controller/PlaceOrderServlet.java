package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.service.PlaceOrderService;
import com.icbt.pahanaedubookshopjavaee.service.impl.PlaceOrderServiceImpl;
import com.icbt.pahanaedubookshopjavaee.util.constants.DBConstants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/place-order")
public class PlaceOrderServlet extends HttpServlet {

    private PlaceOrderService placeOrderService;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute(DBConstants.DBCP_LABEL);
        this.placeOrderService = new PlaceOrderServiceImpl(dataSource);
    }

    /**
     *
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }


    /**
     *
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }


    /**
     *
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}
