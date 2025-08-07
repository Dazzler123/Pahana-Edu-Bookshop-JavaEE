package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.DashboardDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.DashboardDAOImpl;
import com.icbt.pahanaedubookshopjavaee.dto.CustomerAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ItemAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.DashboardStatsDTO;
import com.icbt.pahanaedubookshopjavaee.service.DashboardService;

import javax.sql.DataSource;
import java.util.List;

public class DashboardServiceImpl implements DashboardService {
    
    private final DashboardDAO dashboardDAO;
    private static final int TOP_ITEMS_LIMIT = 5;

    public DashboardServiceImpl(DataSource dataSource) {
        this.dashboardDAO = new DashboardDAOImpl(dataSource);
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        return dashboardDAO.getDashboardStats();
    }

    @Override
    public List<CustomerAnalyticsDTO> getMostVisitedCustomers() {
        return dashboardDAO.getMostVisitedCustomers(TOP_ITEMS_LIMIT);
    }

    @Override
    public List<ItemAnalyticsDTO> getTopSellingItems() {
        return dashboardDAO.getTopSellingItems(TOP_ITEMS_LIMIT);
    }
}