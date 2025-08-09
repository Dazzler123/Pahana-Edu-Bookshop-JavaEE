package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.factory.ServiceFactory;
import com.icbt.pahanaedubookshopjavaee.service.OrderManagementService;
import com.icbt.pahanaedubookshopjavaee.util.constants.DBConstants;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/manage-orders")
public class ManageOrderServlet extends HttpServlet {

    private OrderManagementService orderManagementService;
    private AbstractResponseUtility abstractResponseUtility;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute(DBConstants.DBCP_LABEL);
        ServiceFactory serviceFactory = ServiceFactory.getInstance(dataSource);
        this.orderManagementService = serviceFactory.createOrderManagementService();
        this.abstractResponseUtility = serviceFactory.initiateAbstractUtility();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String customerId = request.getParameter("customerId");

        if (customerId == null || customerId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("message", "Customer ID is required")
                    .build());
            return;
        }

        try {
            JsonObject ordersJson = orderManagementService.getOrdersByCustomer(customerId);
            abstractResponseUtility.writeJson(response, ordersJson);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("message", "Failed to load orders: " + e.getMessage())
                    .build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject json = reader.readObject();

            String orderCode = json.getString("orderCode");
            String orderDate = json.getString("orderDate");

            // Handle numeric values that might come as strings
            double totalAmount;
            double totalDiscount;

            try {
                totalAmount = json.getJsonNumber("totalAmount").doubleValue();
            } catch (ClassCastException e) {
                totalAmount = Double.parseDouble(json.getString("totalAmount"));
            }

            try {
                totalDiscount = json.getJsonNumber("totalDiscount").doubleValue();
            } catch (ClassCastException e) {
                totalDiscount = Double.parseDouble(json.getString("totalDiscount"));
            }

            String status = json.getString("status");
            String paymentStatus = json.getString("paymentStatus");
            String paymentType = json.getString("paymentType", "cash"); // Default to cash if not provided

            orderManagementService.updateOrder(orderCode, orderDate, totalAmount, totalDiscount, status, paymentStatus, paymentType);

            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("message", "Order updated successfully")
                    .build());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("message", "Failed to update order: " + e.getMessage())
                    .build());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject json = reader.readObject();

            String orderCode = json.getString("orderCode");
            String status = json.getString("status");

            orderManagementService.updateOrderStatus(orderCode, status);

            String actionText = status.equals("A") ? "activated" :
                    status.equals("I") ? "inactivated" : "deleted";

            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("message", "Order " + actionText + " successfully")
                    .build());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("message", "Failed to update order status: " + e.getMessage())
                    .build());
        }
    }
}