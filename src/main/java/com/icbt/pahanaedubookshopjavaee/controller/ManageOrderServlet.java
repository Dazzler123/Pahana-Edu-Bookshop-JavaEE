package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.service.OrderManagementService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/manage-orders")
public class ManageOrderServlet extends BaseServlet {

    private OrderManagementService orderManagementService;

    @Override
    protected void initializeServices() {
        this.orderManagementService = serviceFactory.createOrderManagementService();
    }

    /**
     * This method is used to get all the orders for a customer
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String customerId = request.getParameter("customerId");

        JsonObject result = orderManagementService.processGetOrdersRequest(customerId);

        if (result.containsKey("state") && "error".equals(result.getString("state"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        abstractResponseUtility.writeJson(response, result);
    }

    /**
     * This method is used to update an order
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject orderRequest = reader.readObject();

            JsonObject result = orderManagementService.processUpdateOrderRequest(orderRequest);

            if (result.containsKey("state") && "error".equals(result.getString("state"))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            abstractResponseUtility.writeJson(response, result);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", "Invalid request format: " + e.getMessage())
                    .build());
        }
    }

    /**
     * This method is used to update an order's status
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject statusRequest = reader.readObject();

            JsonObject result = orderManagementService.processUpdateOrderStatusRequest(statusRequest);

            if (result.containsKey("state") && "error".equals(result.getString("state"))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            abstractResponseUtility.writeJson(response, result);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", "Invalid request format: " + e.getMessage())
                    .build());
        }
    }

}