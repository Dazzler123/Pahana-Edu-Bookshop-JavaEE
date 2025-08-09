package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.DashboardDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.DashboardDAOImpl;
import com.icbt.pahanaedubookshopjavaee.dto.CustomerAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ItemAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.DashboardStatsDTO;
import com.icbt.pahanaedubookshopjavaee.service.DashboardService;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;
import com.icbt.pahanaedubookshopjavaee.util.constants.ExceptionMessages;

import javax.sql.DataSource;
import java.util.List;

public class DashboardServiceImpl implements DashboardService {
    
    private final DashboardDAO dashboardDAO;
    private static final int TOP_ITEMS_LIMIT = 5;

    public DashboardServiceImpl(DataSource dataSource) {
        this.dashboardDAO = new DashboardDAOImpl(dataSource);
    }

    /**
     * This method is used to get the dashboard statistics
     *
     * @return
     */
    @Override
    public DashboardStatsDTO getDashboardStats() {
        try {
            return dashboardDAO.getDashboardStats();
        } catch (Exception e) {
            throw new RuntimeException(ExceptionMessages.FAILED_TO_LOAD_DASHBOARD_STATISTICS + ": " + e.getMessage(), e);
        }
    }

    /**
     * This method is used to get the most visited customers
     *
     * @return
     */
    @Override
    public List<CustomerAnalyticsDTO> getMostVisitedCustomers() {
        try {
            return dashboardDAO.getMostVisitedCustomers(TOP_ITEMS_LIMIT);
        } catch (Exception e) {
            throw new RuntimeException(ExceptionMessages.FAILED_TO_LOAD_MOST_VISITED_CUSTOMERS + ": " + e.getMessage(), e);
        }
    }

    /**
     * This method is used to get the top selling items
     *
     * @return
     */
    @Override
    public List<ItemAnalyticsDTO> getTopSellingItems() {
        try {
            return dashboardDAO.getTopSellingItems(TOP_ITEMS_LIMIT);
        } catch (Exception e) {
            throw new RuntimeException(ExceptionMessages.FAILED_TO_LOAD_TOP_SELLING_ITEMS + ": " + e.getMessage(), e);
        }
    }

}