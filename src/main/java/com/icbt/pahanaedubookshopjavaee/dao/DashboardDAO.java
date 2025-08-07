package com.icbt.pahanaedubookshopjavaee.dao;

import com.icbt.pahanaedubookshopjavaee.dto.CustomerAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ItemAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.DashboardStatsDTO;

import java.util.List;

public interface DashboardDAO {
    DashboardStatsDTO getDashboardStats();
    List<CustomerAnalyticsDTO> getMostVisitedCustomers(int limit);
    List<ItemAnalyticsDTO> getTopSellingItems(int limit);
}