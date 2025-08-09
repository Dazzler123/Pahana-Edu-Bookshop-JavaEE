package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.dto.CustomerAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.dto.ItemAnalyticsDTO;
import com.icbt.pahanaedubookshopjavaee.service.DashboardService;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard-analytics")
public class DashboardAnalyticsServlet extends BaseServlet {

    private DashboardService dashboardService;

    @Override
    protected void initializeServices() {
        this.dashboardService = serviceFactory.createDashboardService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = request.getParameter("type");

        try {
            if ("most-visited-customers".equals(type)) {
                getMostVisitedCustomers(response);
            } else if ("top-selling-items".equals(type)) {
                getTopSellingItems(response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                        .add("error", "Invalid type parameter")
                        .build());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("error", "Failed to load analytics data: " + e.getMessage())
                    .build());
        }
    }

    private void getMostVisitedCustomers(HttpServletResponse response) throws Exception {
        List<CustomerAnalyticsDTO> customers = dashboardService.getMostVisitedCustomers();
        JsonArrayBuilder customersArray = Json.createArrayBuilder();

        int rank = 1;
        for (CustomerAnalyticsDTO customer : customers) {
            customersArray.add(Json.createObjectBuilder()
                    .add("rank", rank++)
                    .add("accountNumber", customer.getAccountNumber())
                    .add("name", customer.getName())
                    .add("orderCount", customer.getOrderCount())
                    .add("totalSpent", customer.getTotalSpent())
            );
        }

        JsonObject json = Json.createObjectBuilder()
                .add("customers", customersArray)
                .build();

        abstractResponseUtility.writeJson(response, json);
    }

    private void getTopSellingItems(HttpServletResponse response) throws Exception {
        List<ItemAnalyticsDTO> items = dashboardService.getTopSellingItems();
        JsonArrayBuilder itemsArray = Json.createArrayBuilder();

        int rank = 1;
        for (ItemAnalyticsDTO item : items) {
            itemsArray.add(Json.createObjectBuilder()
                    .add("rank", rank++)
                    .add("itemCode", item.getItemCode())
                    .add("name", item.getName())
                    .add("totalSold", item.getTotalSold())
                    .add("totalRevenue", item.getTotalRevenue())
            );
        }

        JsonObject json = Json.createObjectBuilder()
                .add("items", itemsArray)
                .build();

        abstractResponseUtility.writeJson(response, json);
    }
}