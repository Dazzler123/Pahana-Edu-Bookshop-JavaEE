package com.icbt.pahanaedubookshopjavaee.dao;

import com.icbt.pahanaedubookshopjavaee.model.Item;

import java.util.List;

public interface ItemDAO {
    List<Item> findAll();

    void save(Item c);

    void update(Item c);

    boolean exists(String accountNumber);

    void updateStatus(String acc, char status);

    char getStatus(String itemCode);
}
