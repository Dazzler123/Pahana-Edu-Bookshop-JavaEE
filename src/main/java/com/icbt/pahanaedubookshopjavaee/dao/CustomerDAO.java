package com.icbt.pahanaedubookshopjavaee.dao;

import com.icbt.pahanaedubookshopjavaee.model.Customer;

import java.util.List;

public interface CustomerDAO {

    List<Customer> findAll();

    void save(Customer c);

    void update(Customer c);

    boolean exists(String accountNumber);

    void updateStatus(String acc, char status);

    char getStatus(String accountNumber);
}
