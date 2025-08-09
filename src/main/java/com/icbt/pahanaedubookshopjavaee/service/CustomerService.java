package com.icbt.pahanaedubookshopjavaee.service;

import com.icbt.pahanaedubookshopjavaee.model.Customer;

import javax.json.JsonObject;
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

    JsonObject getAllCustomersAsJson();

    JsonObject getAllCustomerIdsAsJson();

    JsonObject getCustomerByAccountNumberAsJson(String accountNumber);

    JsonObject generateNextAccountNumberAsJson();

    JsonObject saveOrUpdateCustomer(String accountNumber, String name, String address, String telephone, String status);

    JsonObject updateCustomerStatus(String accountNumber, String status);
}
