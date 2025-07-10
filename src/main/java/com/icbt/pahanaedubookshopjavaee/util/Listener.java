package com.icbt.pahanaedubookshopjavaee.util;

import com.icbt.pahanaedubookshopjavaee.util.constants.DBConstants;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Listener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute(DBConstants.DBCP_LABEL, DBUtil.getInstance());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            ((BasicDataSource) DBUtil.getInstance()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

