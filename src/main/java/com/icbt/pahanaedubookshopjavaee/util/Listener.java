package com.icbt.pahanaedubookshopjavaee.util;

import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
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
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName(DBConstants.DB_DRIVER);
        bds.setUrl(DBConstants.DB_URL + DBConstants.DEPLOYMENT_PORT + CommonConstants.SLASH_STRING + DBConstants.DB_NAME);
        bds.setUsername(DBConstants.DB_USERNAME);
        bds.setPassword(DBConstants.DB_PASSWORD);
        bds.setMaxTotal(2);
        bds.setInitialSize(2);
        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute(DBConstants.DBCP_LABEL, bds);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
