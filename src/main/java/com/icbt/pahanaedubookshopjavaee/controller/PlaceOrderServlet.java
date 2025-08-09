package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.dto.PlaceOrderDTO;
import com.icbt.pahanaedubookshopjavaee.model.OrderItem;
import com.icbt.pahanaedubookshopjavaee.service.PlaceOrderService;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;

import javax.json.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/place-order")
public class PlaceOrderServlet extends BaseServlet {

    private PlaceOrderService placeOrderService;

    @Override
    protected void initializeServices() {
        this.placeOrderService = serviceFactory.createPlaceOrderService();
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

            // determine payment status based on payment method
            String paymentStatus;

            if (CommonConstants.PAYMENT_METHOD_CASH.equals(paymentMethod)) {
                paymentStatus = CommonConstants.PAYMENT_STATUS_PAID;
            } else if (CommonConstants.PAYMENT_METHOD_CARD.equals(paymentMethod)) {
                paymentStatus = CommonConstants.PAYMENT_STATUS_PENDING;
            } else if (CommonConstants.PAYMENT_METHOD_OTHER.equals(paymentMethod)) {
                paymentStatus = CommonConstants.PAYMENT_STATUS_NOT_PAID;
            } else {
                paymentStatus = CommonConstants.PAYMENT_STATUS_NOT_PAID;
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

            PlaceOrderDTO placeOrderDTO = new PlaceOrderDTO(
                    customerId,
                    totalAmount,
                    totalDiscount,
                    itemList,
                    paymentStatus,
                    paymentMethod
            );

            String orderCode = placeOrderService.placeOrder(placeOrderDTO);
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
