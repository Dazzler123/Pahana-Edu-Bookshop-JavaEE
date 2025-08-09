package com.icbt.pahanaedubookshopjavaee.service;

import com.icbt.pahanaedubookshopjavaee.model.Item;

import javax.json.JsonObject;
import java.util.List;

public interface ItemService {
    List<Item> getAllItems();

    boolean isItemExists(String itemCode);

    void saveItem(Item item);

    void updateItem(Item item);

    void updateStatus(String itemCode, char status);

    String generateNextItemCode();

    JsonObject getAllItemsAsJson();

    JsonObject generateNextItemCodeAsJson();

    JsonObject saveOrUpdateItem(String itemCode, String name, String unitPrice, String qtyOnHand, String status);

    JsonObject updateItemStatus(String itemCode, String status);
}
