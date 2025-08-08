package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.model.OrderItem;
import com.icbt.pahanaedubookshopjavaee.service.PlaceOrderService;
import com.icbt.pahanaedubookshopjavaee.service.impl.PlaceOrderServiceImpl;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;
import com.icbt.pahanaedubookshopjavaee.util.constants.DBConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;

import javax.json.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/place-order")
public class PlaceOrderServlet extends HttpServlet {

    private PlaceOrderService placeOrderService;
    private AbstractResponseUtility abstractResponseUtility;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute(DBConstants.DBCP_LABEL);
        this.placeOrderService = new PlaceOrderServiceImpl(dataSource);
        this.abstractResponseUtility = new AbstractResponseUtility();
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }


    /**
     * This method can be used to create/place new order
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject json = reader.readObject();

            String customerId = json.getString("customerAccount", null);
            String paymentMethod = json.getString("paymentMethod", null);
            JsonArray itemsArray = json.getJsonArray("items");

            if (customerId == null || paymentMethod == null || itemsArray == null || itemsArray.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", "Incomplete order data.")
                        .build());
                return;
            }

            // Determine payment status based on payment method
            String paymentStatus;
            if ("cash".equalsIgnoreCase(paymentMethod)) {
                paymentStatus = CommonConstants.PAYMENT_STATUS_PAID; // "A" for Paid
            } else if ("card".equalsIgnoreCase(paymentMethod)) {
                paymentStatus = CommonConstants.PAYMENT_STATUS_PENDING; // "P" for Pending  
            } else {
                paymentStatus = CommonConstants.PAYMENT_STATUS_NOT_PAID; // default as not paid
            }

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            List<OrderItem> itemList = new ArrayList<>();

            for (JsonValue val : itemsArray) {
                JsonObject itemJson = val.asJsonObject();
                String itemCode = itemJson.getString("itemCode");
                int qty = itemJson.getInt("qty");

                BigDecimal unitPrice = new BigDecimal(itemJson.get("unitPrice").toString());
                BigDecimal discountPercentage = new BigDecimal(itemJson.get("discount").toString()); // as %

                // Calculate line total and discount amount
                BigDecimal lineTotalBeforeDiscount = unitPrice.multiply(BigDecimal.valueOf(qty));
                BigDecimal discountAmount = lineTotalBeforeDiscount
                        .multiply(discountPercentage)
                        .divide(BigDecimal.valueOf(100)); // Convert % to fraction

                BigDecimal lineTotal = lineTotalBeforeDiscount.subtract(discountAmount);

                totalAmount = totalAmount.add(lineTotal);
                totalDiscount = totalDiscount.add(discountAmount);

                itemList.add(new OrderItem(itemCode, qty, unitPrice, discountAmount, lineTotal));
            }

            String orderCode = placeOrderService.placeOrder(customerId, totalAmount, totalDiscount, itemList, paymentStatus);
            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("state", "done")
                    .add("message", "Order placed successfully. Order Code: " + orderCode)
                    .add("orderCode", orderCode)
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", "Failed to place order: " + e.getMessage())
                    .build());
        }
    }


    /**
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}
