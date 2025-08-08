package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.ItemDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.ItemDAOImpl;
import com.icbt.pahanaedubookshopjavaee.model.Item;
import com.icbt.pahanaedubookshopjavaee.service.ItemService;

import javax.sql.DataSource;
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
}
