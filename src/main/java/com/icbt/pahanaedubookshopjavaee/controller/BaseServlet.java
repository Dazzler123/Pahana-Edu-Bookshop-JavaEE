package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.factory.ServiceFactory;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;
import com.icbt.pahanaedubookshopjavaee.util.constants.DBConstants;

import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

/**
 * This class is the base class for all servlets that need to access the database
 * This class provides the necessary initialization for the database connection
 * and the service factory.
 * (Implementation of Template Method Pattern)
 *
 */
public abstract class BaseServlet extends HttpServlet {
    
    protected ServiceFactory serviceFactory;
    protected AbstractResponseUtility abstractResponseUtility;
    
    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute(DBConstants.DBCP_LABEL);
        this.serviceFactory = ServiceFactory.getInstance(dataSource);
        this.abstractResponseUtility = serviceFactory.initiateAbstractUtility();
        
        // call child servlet's initialization
        initializeServices();
    }
    
    /**
     * Override this method in child servlets to initialize specific services
     */
    protected abstract void initializeServices();
}