package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.dto.DashboardStatsDTO;
import com.icbt.pahanaedubookshopjavaee.service.DashboardService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/dashboard-stats")
public class DashboardStatsServlet extends BaseServlet {

    private DashboardService dashboardService;

    @Override
    protected void initializeServices() {
        this.dashboardService = serviceFactory.createDashboardService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();

            JsonObject json = Json.createObjectBuilder()
                    .add("totalOrders", stats.getTotalOrders())
                    .add("pendingOrders", stats.getPendingOrders())
                    .add("totalRevenue", stats.getTotalRevenue())
                    .build();

            abstractResponseUtility.writeJson(response, json);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("error", "Failed to load dashboard statistics: " + e.getMessage())
                    .build());
        }
    }
}