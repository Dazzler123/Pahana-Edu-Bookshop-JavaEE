package com.icbt.pahanaedubookshopjavaee.service;

import com.icbt.pahanaedubookshopjavaee.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getAllCustomers();

    List<String> getAllCustomerIds();

    Customer getCustomerById(String accountNumber);

    boolean isExistingCustomer(String accountNumber);

    void saveCustomer(Customer customer);

    void updateCustomer(Customer customer);

    void updateStatus(String accountNumber, char status);

    String generateNextAccountNumber();
}
