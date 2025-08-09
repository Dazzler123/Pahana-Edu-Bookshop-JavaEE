package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.service.PlaceOrderService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/place-order")
public class PlaceOrderServlet extends BaseServlet {

    private PlaceOrderService placeOrderService;

    @Override
    protected void initializeServices() {
        this.placeOrderService = serviceFactory.createPlaceOrderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "GET method not supported for place-order")
                .build());
    }

    /**
     * This method is used to place a new purchase order
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject orderRequest = reader.readObject();
            
            JsonObject result = placeOrderService.processOrderRequest(orderRequest);
            
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

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                .add("state", "error")
                .add("message", "PUT method not supported for place-order")
                .build());
    }
}