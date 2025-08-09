package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.ItemDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.ItemDAOImpl;
import com.icbt.pahanaedubookshopjavaee.model.Item;
import com.icbt.pahanaedubookshopjavaee.service.ItemService;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;
import com.icbt.pahanaedubookshopjavaee.util.constants.ExceptionMessages;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

public class ItemServiceImpl implements ItemService {

    private final ItemDAO itemDAO;

    public ItemServiceImpl(DataSource dataSource) {
        this.itemDAO = new ItemDAOImpl(dataSource);
    }

    @Override
    public List<Item> getAllItems() {
        return itemDAO.findAll();
    }

    @Override
    public boolean isItemExists(String itemCode) {
        return itemDAO.exists(itemCode);
    }

    @Override
    public void saveItem(Item item) {
        itemDAO.save(item);
    }

    @Override
    public void updateItem(Item item) {
        itemDAO.update(item);
    }

    @Override
    public void updateStatus(String itemCode, char status) {
        itemDAO.updateStatus(itemCode, status);
    }

    @Override
    public String generateNextItemCode() {
        return itemDAO.generateNextItemCode();
    }

    /**
     * This method is used to get all items
     *
     * @return
     */
    @Override
    public JsonObject getAllItemsAsJson() {
        try {
            List<Item> items = getAllItems();
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

            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_DONE)
                    .add("items", itemArray)
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                    .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_FAILED_TO_RETRIEVE_ITEMS + ": " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to generate the next new unique item code
     *
     * @return
     */
    @Override
    public JsonObject generateNextItemCodeAsJson() {
        try {
            String nextItemCode = generateNextItemCode();
            return Json.createObjectBuilder()
                    .add("itemCode", nextItemCode)
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                    .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_FAILED_TO_GENERATE_ITEM_CODE + ": " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to save or update an item
     *
     * @param itemCode
     * @param name
     * @param unitPrice
     * @param qtyOnHand
     * @param status
     * @return
     */
    @Override
    public JsonObject saveOrUpdateItem(String itemCode, String name, String unitPrice, String qtyOnHand, String status) {
        try {
            // Validation
            if (name == null || name.trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_ITEM_NAME_REQUIRED)
                        .build();
            }

            if (name.length() > 100) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_ITEM_NAME_TOO_LONG)
                        .build();
            }

            if (unitPrice == null || unitPrice.trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_UNIT_PRICE_REQUIRED)
                        .build();
            }

            if (qtyOnHand == null || qtyOnHand.trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_QTY_ON_HAND_REQUIRED)
                        .build();
            }

            // Parse and validate numeric values
            BigDecimal price;
            int qty;

            try {
                price = new BigDecimal(unitPrice);
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    return Json.createObjectBuilder()
                            .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                            .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_UNIT_PRICE_MUST_BE_POSITIVE)
                            .build();
                }
            } catch (NumberFormatException e) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_INVALID_UNIT_PRICE_FORMAT)
                        .build();
            }

            try {
                qty = Integer.parseInt(qtyOnHand);
                if (qty < 0) {
                    return Json.createObjectBuilder()
                            .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                            .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_QTY_ON_HAND_CANNOT_BE_NEGATIVE)
                            .build();
                }
            } catch (NumberFormatException e) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_INVALID_QUANTITY_FORMAT)
                        .build();
            }

            // Generate item code if not provided
            if (itemCode == null || itemCode.trim().isEmpty()) {
                itemCode = generateNextItemCode();
            }

            // Set default status if not provided
            char statusChar = (status != null && !status.isEmpty()) ?
                    status.charAt(0) : CommonConstants.STATUS_ACTIVE_CHAR;

            Item item = new Item(itemCode, name, price, qty, statusChar);

            boolean exists = isItemExists(itemCode);
            if (exists) {
                updateItem(item);
            } else {
                saveItem(item);
            }

            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_DONE)
                    .add(CommonConstants.LABEL_MESSAGE, exists ?
                            ResponseMessages.MESSAGE_ITEM_UPDATED_SUCCESSFULLY :
                            ResponseMessages.MESSAGE_ITEM_SAVED_SUCCESSFULLY)
                    .add("itemCode", itemCode)
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                    .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_FAILED_TO_SAVE_ITEM + ": " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to update the item's status
     *
     * @param itemCode
     * @param status
     * @return
     */
    @Override
    public JsonObject updateItemStatus(String itemCode, String status) {
        try {
            // Validation
            if (itemCode == null || itemCode.trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, "Item code is required")
                        .build();
            }

            if (status == null || !status.matches("[AID]")) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.INVALID_REQUEST_PAYLOAD)
                        .build();
            }

            // Check if item exists
            if (!isItemExists(itemCode)) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.MESSAGE_ITEM_NOT_FOUND)
                        .build();
            }

            updateStatus(itemCode, status.charAt(0));

            String actionText = status.equals(CommonConstants.STATUS_ACTIVE_STRING) ? "activated" :
                    status.equals(CommonConstants.STATUS_INACTIVE_STRING) ? "inactivated" :
                            "deleted";

            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_DONE)
                    .add(CommonConstants.LABEL_MESSAGE,
                            ResponseMessages.MESSAGE_ITEM_STATUS_UPDATED.replace(CommonConstants.REPLACER, actionText))
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                    .add(CommonConstants.LABEL_MESSAGE, ExceptionMessages.FAILED_TO_UPDATE_ITEM_STATUS + ": " + e.getMessage())
                    .build();
        }
    }

}