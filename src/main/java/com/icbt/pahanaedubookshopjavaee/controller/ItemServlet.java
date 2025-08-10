package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.service.ItemService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/item")
public class ItemServlet extends BaseServlet {

    private ItemService itemService;

    @Override
    protected void initializeServices() {
        this.itemService = serviceFactory.createItemService();
    }

    /**
     * This method is used to get all the available items
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        JsonObject result;

        if ("generateItemCode".equals(action)) {
            result = itemService.generateNextItemCodeAsJson();
        } else {
            result = itemService.getAllItemsAsJson();
        }

        abstractResponseUtility.writeJson(response, result);
    }

    /**
     * This method is used to save or update an item
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String itemCode = request.getParameter("item_code");
        String name = request.getParameter("name");
        String unitPrice = request.getParameter("unit_price");
        String qtyOnHand = request.getParameter("qty_on_hand");
        String status = request.getParameter("status");

        JsonObject result = itemService.saveOrUpdateItem(itemCode, name, unitPrice, qtyOnHand, status);
        
        if (result.containsKey("state") && "error".equals(result.getString("state"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        abstractResponseUtility.writeJson(response, result);
    }

    /**
     * This method is used to update the item's status
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject json = reader.readObject();
            String itemCode = json.getString("item_code", null);
            String status = json.getString("status", null);

            JsonObject result = itemService.updateItemStatus(itemCode, status);
            
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