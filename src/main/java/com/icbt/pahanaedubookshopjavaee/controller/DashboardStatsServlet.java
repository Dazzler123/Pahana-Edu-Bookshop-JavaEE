package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.dto.DashboardStatsDTO;
import com.icbt.pahanaedubookshopjavaee.factory.ServiceFactory;
import com.icbt.pahanaedubookshopjavaee.service.DashboardService;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;
import com.icbt.pahanaedubookshopjavaee.util.constants.DBConstants;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/dashboard-stats")
public class DashboardStatsServlet extends HttpServlet {

    private DashboardService dashboardService;
    private AbstractResponseUtility abstractResponseUtility;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute(DBConstants.DBCP_LABEL);
        ServiceFactory serviceFactory = ServiceFactory.getInstance(dataSource);
        this.dashboardService = serviceFactory.createDashboardService();
        this.abstractResponseUtility = serviceFactory.initiateAbstractUtility();
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