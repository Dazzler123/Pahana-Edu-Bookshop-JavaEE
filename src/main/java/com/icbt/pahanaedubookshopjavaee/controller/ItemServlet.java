package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.model.Item;
import com.icbt.pahanaedubookshopjavaee.service.ItemService;
import com.icbt.pahanaedubookshopjavaee.service.impl.ItemServiceImpl;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.DBConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;

import javax.json.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/item")
public class ItemServlet extends HttpServlet {

    private ItemService itemService;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute(DBConstants.DBCP_LABEL);
        this.itemService = new ItemServiceImpl(dataSource);
    }

    /**
     * This method is used to get all the available items
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Item> items = itemService.getAllItems();

        JsonArrayBuilder itemArray = Json.createArrayBuilder();
        for (Item i : items) {
            itemArray.add(Json.createObjectBuilder()
                    .add("itemCode", i.getItemCode())
                    .add("name", i.getName())
                    .add("unitPrice", i.getUnitPrice())
                    .add("qtyOnHand", i.getQtyOnHand())
                    .add("status", String.valueOf(i.getStatus()))
            );
        }

        JsonObject json = Json.createObjectBuilder()
                .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_DONE)
                .add("items", itemArray)
                .build();

        writeJson(response, json);
    }


    /**
     * This method is used to create/save a new item
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("item_code");
        String name = request.getParameter("name");
        BigDecimal price = new BigDecimal(request.getParameter("unit_price"));
        int qty = Integer.parseInt(request.getParameter("qty_on_hand"));
        char status = request.getParameter("status") != null ?
                request.getParameter("status").charAt(0) : CommonConstants.STATUS_ACTIVE_CHAR;

        Item item = new Item(code, name, price, qty, status);

        boolean exists = itemService.isItemExists(code);
        if (exists) {
            itemService.updateItem(item);
        } else {
            itemService.saveItem(item);
        }

        JsonObject json = Json.createObjectBuilder()
                .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_DONE)
                .add(CommonConstants.LABEL_MESSAGE, exists ? ResponseMessages.MESSAGE_ITEM_UPDATED_SUCCESSFULLY :
                        ResponseMessages.MESSAGE_ITEM_SAVED_SUCCESSFULLY)
                .build();

        writeJson(response, json);
    }


    /**
     * This method is used to update the item's status
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject json = reader.readObject();
            String code = json.getString("item_code", null);
            String stat = json.getString("status", null);

            if (code == null || stat == null || !stat.matches("[AID]")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJson(response, Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.INVALID_REQUEST_PAYLOAD)
                        .build());
                return;
            }

            itemService.updateStatus(code, stat.charAt(0));

            String actionText = stat.equals(CommonConstants.STATUS_ACTIVE_STRING) ? "activated" :
                    stat.equals(CommonConstants.STATUS_INACTIVE_STRING) ? "inactivated" :
                            "deleted";

            writeJson(response, Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_DONE)
                    .add(CommonConstants.LABEL_MESSAGE,
                            ResponseMessages.MESSAGE_ITEM_STATUS_UPDATED.replace(CommonConstants.REPLACER, actionText))
                    .build());
        }
    }

    /**
     * This method is used to compile the final common response as a JSON
     *
     * @param response
     * @param data
     * @throws IOException
     */
    private void writeJson(HttpServletResponse response, JsonObject data) throws IOException {
        response.setContentType(CommonConstants.MIME_TYPE_JSON);
        response.getWriter().print(data.toString());
    }

}