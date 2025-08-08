package com.icbt.pahanaedubookshopjavaee.service;

import com.icbt.pahanaedubookshopjavaee.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getAllItems();

    boolean isItemExists(String itemCode);

    void saveItem(Item item);

    void updateItem(Item item);

    void updateStatus(String itemCode, char status);

    String generateNextItemCode();
}
