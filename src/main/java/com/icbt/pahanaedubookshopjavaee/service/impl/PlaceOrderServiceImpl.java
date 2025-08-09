package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.PlaceOrderDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.PlaceOrderDAOImpl;
import com.icbt.pahanaedubookshopjavaee.dto.PlaceOrderDTO;
import com.icbt.pahanaedubookshopjavaee.model.OrderItem;
import com.icbt.pahanaedubookshopjavaee.service.PlaceOrderService;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;
import com.icbt.pahanaedubookshopjavaee.util.constants.ExceptionMessages;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderServiceImpl implements PlaceOrderService {

    private final PlaceOrderDAO placeOrderDAO;

    public PlaceOrderServiceImpl(DataSource dataSource) {
        this.placeOrderDAO = new PlaceOrderDAOImpl(dataSource);
    }

    @Override
    public String placeOrder(PlaceOrderDTO placeOrderDTO) throws Exception {
        return placeOrderDAO.createOrder(
            placeOrderDTO.getCustomerId(),
            placeOrderDTO.getTotalAmount(),
            placeOrderDTO.getTotalDiscount(),
            placeOrderDTO.getOrderItems(),
            placeOrderDTO.getPaymentStatus(),
            placeOrderDTO.getPaymentMethod()
        );
    }

    /**
     * This method is used to process the order request
     *
     * @param orderRequest
     * @return
     */
    @Override
    public JsonObject processOrderRequest(JsonObject orderRequest) {
        try {
            // Extract and validate basic order data
            String customerId = orderRequest.getString("customerAccount", null);
            String paymentMethod = orderRequest.getString("paymentMethod", null);
            JsonArray itemsArray = orderRequest.getJsonArray("items");

            // Validation
            if (customerId == null || customerId.trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_CUSTOMER_ACCOUNT_REQUIRED)
                        .build();
            }

            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_PAYMENT_METHOD_REQUIRED)
                        .build();
            }

            if (itemsArray == null || itemsArray.isEmpty()) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_ORDER_ITEMS_REQUIRED)
                        .build();
            }

            // Validate payment method
            if (!isValidPaymentMethod(paymentMethod)) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_INVALID_PAYMENT_METHOD)
                        .build();
            }

            // Determine payment status based on payment method
            String paymentStatus = determinePaymentStatus(paymentMethod);

            // Process order items and calculate totals
            List<OrderItem> itemList = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;

            for (JsonValue val : itemsArray) {
                JsonObject itemJson = val.asJsonObject();
                
                // Validate item data
                JsonObject itemValidation = validateOrderItem(itemJson);
                if (itemValidation.containsKey("error")) {
                    return itemValidation;
                }

                String itemCode = itemJson.getString("itemCode");
                int qty = itemJson.getInt("qty");
                BigDecimal unitPrice = new BigDecimal(itemJson.get("unitPrice").toString());
                BigDecimal discountPercentage = new BigDecimal(itemJson.get("discount").toString());

                // Validate discount percentage
                if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || 
                    discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
                    return Json.createObjectBuilder()
                            .add("state", "error")
                            .add("message", ResponseMessages.MESSAGE_DISCOUNT_PERCENTAGE_INVALID.replace(CommonConstants.REPLACER, itemCode))
                            .build();
                }

                // Calculate line totals
                BigDecimal lineTotalBeforeDiscount = unitPrice.multiply(BigDecimal.valueOf(qty));
                BigDecimal discountAmount = lineTotalBeforeDiscount
                        .multiply(discountPercentage)
                        .divide(BigDecimal.valueOf(100));
                BigDecimal lineTotal = lineTotalBeforeDiscount.subtract(discountAmount);

                // Validate calculated amounts
                if (lineTotal.compareTo(BigDecimal.ZERO) < 0) {
                    return Json.createObjectBuilder()
                            .add("state", "error")
                            .add("message", ResponseMessages.MESSAGE_INVALID_LINE_TOTAL_CALCULATION.replace(CommonConstants.REPLACER, itemCode))
                            .build();
                }

                totalAmount = totalAmount.add(lineTotal);
                totalDiscount = totalDiscount.add(discountAmount);

                itemList.add(new OrderItem(itemCode, qty, unitPrice, discountAmount, lineTotal));
            }

            // Final validation
            if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return Json.createObjectBuilder()
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_ORDER_TOTAL_MUST_BE_POSITIVE)
                        .build();
            }

            // Create order DTO
            PlaceOrderDTO placeOrderDTO = new PlaceOrderDTO(
                    customerId,
                    totalAmount,
                    totalDiscount,
                    itemList,
                    paymentStatus,
                    paymentMethod
            );

            // Place the order
            String orderCode = placeOrder(placeOrderDTO);

            return Json.createObjectBuilder()
                    .add("state", "done")
                    .add("message", ResponseMessages.MESSAGE_ORDER_PLACED_SUCCESSFULLY.replace(CommonConstants.REPLACER, orderCode))
                    .add("orderCode", orderCode)
                    .add("totalAmount", totalAmount)
                    .add("totalDiscount", totalDiscount)
                    .add("paymentStatus", paymentStatus)
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", ExceptionMessages.FAILED_TO_PLACE_ORDER + ": " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method can be used to validate the provided payment method
     *
     * @param paymentMethod
     * @return
     */
    private boolean isValidPaymentMethod(String paymentMethod) {
        return CommonConstants.PAYMENT_METHOD_CASH.equals(paymentMethod) ||
               CommonConstants.PAYMENT_METHOD_CARD.equals(paymentMethod) ||
               CommonConstants.PAYMENT_METHOD_OTHER.equals(paymentMethod);
    }

    /**
     * This method can be used to determine the payment status based on the payment method
     *
     * @param paymentMethod
     * @return
     */
    private String determinePaymentStatus(String paymentMethod) {
        if (CommonConstants.PAYMENT_METHOD_CASH.equals(paymentMethod)) {
            return CommonConstants.PAYMENT_STATUS_PAID;
        } else if (CommonConstants.PAYMENT_METHOD_CARD.equals(paymentMethod)) {
            return CommonConstants.PAYMENT_STATUS_PENDING;
        } else if (CommonConstants.PAYMENT_METHOD_OTHER.equals(paymentMethod)) {
            return CommonConstants.PAYMENT_STATUS_NOT_PAID;
        } else {
            return CommonConstants.PAYMENT_STATUS_NOT_PAID;
        }
    }

    /**
     * This method can be used to validate the provided order item
     *
     * @param itemJson
     * @return
     */
    private JsonObject validateOrderItem(JsonObject itemJson) {
        try {
            // Validate required fields
            if (!itemJson.containsKey("itemCode") || itemJson.getString("itemCode").trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add("error", true)
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_ITEM_CODE_REQUIRED_FOR_ORDER)
                        .build();
            }

            if (!itemJson.containsKey("qty")) {
                return Json.createObjectBuilder()
                        .add("error", true)
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_QUANTITY_REQUIRED_FOR_ORDER.replace(CommonConstants.REPLACER, itemJson.getString("itemCode")))
                        .build();
            }

            if (!itemJson.containsKey("unitPrice")) {
                return Json.createObjectBuilder()
                        .add("error", true)
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_UNIT_PRICE_REQUIRED_FOR_ORDER.replace(CommonConstants.REPLACER, itemJson.getString("itemCode")))
                        .build();
            }

            if (!itemJson.containsKey("discount")) {
                return Json.createObjectBuilder()
                        .add("error", true)
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_DISCOUNT_REQUIRED_FOR_ORDER.replace(CommonConstants.REPLACER, itemJson.getString("itemCode")))
                        .build();
            }

            // Validate numeric values
            int qty = itemJson.getInt("qty");
            if (qty <= 0) {
                return Json.createObjectBuilder()
                        .add("error", true)
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_QUANTITY_MUST_BE_POSITIVE.replace(CommonConstants.REPLACER, itemJson.getString("itemCode")))
                        .build();
            }

            BigDecimal unitPrice = new BigDecimal(itemJson.get("unitPrice").toString());
            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                return Json.createObjectBuilder()
                        .add("error", true)
                        .add("state", "error")
                        .add("message", ResponseMessages.MESSAGE_UNIT_PRICE_MUST_BE_POSITIVE_FOR_ORDER.replace(CommonConstants.REPLACER, itemJson.getString("itemCode")))
                        .build();
            }

            return Json.createObjectBuilder()
                    .add("valid", true)
                    .build();

        } catch (NumberFormatException e) {
            return Json.createObjectBuilder()
                    .add("error", true)
                    .add("state", "error")
                    .add("message", ResponseMessages.MESSAGE_INVALID_NUMERIC_VALUE_IN_ITEM)
                    .build();
        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("error", true)
                    .add("state", "error")
                    .add("message", ResponseMessages.MESSAGE_INVALID_ITEM_DATA_FORMAT)
                    .build();
        }
    }

}
