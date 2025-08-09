package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.factory.ServiceFactory;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;

import javax.servlet.http.HttpServlet;

/**
 * This class is used to create stateless servlets (Implementation of Template Method Pattern)
 * This class is used to create servlets that do not require a database connection
 */
public abstract class BaseStatelessServlet extends HttpServlet {
    
    protected ServiceFactory serviceFactory;
    protected AbstractResponseUtility abstractResponseUtility;
    
    @Override
    public void init() {
        this.serviceFactory = ServiceFactory.getInstance(null);
        this.abstractResponseUtility = serviceFactory.initiateAbstractUtility();
        
        initializeServices();
    }
    
    protected abstract void initializeServices();
}