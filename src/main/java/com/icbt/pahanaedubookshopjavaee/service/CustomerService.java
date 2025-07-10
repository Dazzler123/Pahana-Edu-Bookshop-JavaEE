package com.icbt.pahanaedubookshopjavaee.service;

import com.icbt.pahanaedubookshopjavaee.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getAllCustomers();

    boolean isExistingCustomer(String accountNumber);

    void saveCustomer(Customer customer);

    void updateCustomer(Customer customer);

    void updateStatus(String accountNumber, char status);
}
