package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.CustomerDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.CustomerDAOImpl;
import com.icbt.pahanaedubookshopjavaee.model.Customer;
import com.icbt.pahanaedubookshopjavaee.service.CustomerService;

import javax.sql.DataSource;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerServiceImpl(DataSource dataSource) {
        this.customerDAO = new CustomerDAOImpl(dataSource);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }

    @Override
    public List<String> getAllCustomerIds() {
        return customerDAO.getAllIds();
    }

    @Override
    public Customer getCustomerById(String accountNumber) {
        return customerDAO.getCustomer(accountNumber);
    }

    @Override
    public boolean isExistingCustomer(String accountNumber) {
        return customerDAO.exists(accountNumber);
    }

    @Override
    public void saveCustomer(Customer customer) {
        customerDAO.save(customer);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customerDAO.update(customer);
    }

    @Override
    public void updateStatus(String accountNumber, char status) {
        customerDAO.updateStatus(accountNumber, status);
    }
}
