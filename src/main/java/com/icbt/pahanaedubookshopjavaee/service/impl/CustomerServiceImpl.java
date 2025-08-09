package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.CustomerDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.CustomerDAOImpl;
import com.icbt.pahanaedubookshopjavaee.model.Customer;
import com.icbt.pahanaedubookshopjavaee.service.CustomerService;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
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

    @Override
    public String generateNextAccountNumber() {
        return customerDAO.generateNextAccountNumber();
    }

    /**
     * This method is used to get all customers
     *
     * @return
     */
    @Override
    public JsonObject getAllCustomersAsJson() {
        try {
            List<Customer> customers = getAllCustomers();
            JsonArrayBuilder customerArray = Json.createArrayBuilder();

            for (Customer c : customers) {
                customerArray.add(Json.createObjectBuilder()
                        .add("accountNumber", c.getAccountNumber())
                        .add("name", c.getName())
                        .add("address", c.getAddress())
                        .add("telephone", c.getTelephone())
                        .add("status", String.valueOf(c.getStatus())));
            }

            return Json.createObjectBuilder()
                    .add("customers", customerArray)
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                    .add(CommonConstants.LABEL_MESSAGE, "Failed to retrieve customers: " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to get all customer IDs
     *
     * @return
     */
    @Override
    public JsonObject getAllCustomerIdsAsJson() {
        try {
            List<String> ids = getAllCustomerIds();
            JsonArrayBuilder idArray = Json.createArrayBuilder();
            ids.forEach(id -> idArray.add(id));

            return Json.createObjectBuilder()
                    .add("customerIds", idArray)
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                    .add(CommonConstants.LABEL_MESSAGE, "Failed to retrieve customer IDs: " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to get customer using account number
     *
     * @param accountNumber
     * @return
     */
    @Override
    public JsonObject getCustomerByAccountNumberAsJson(String accountNumber) {
        try {
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, "Account number is required")
                        .build();
            }

            Customer customer = getCustomerById(accountNumber);
            if (customer == null) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, "Customer not found")
                        .build();
            }

            return Json.createObjectBuilder()
                    .add("accountNumber", customer.getAccountNumber())
                    .add("name", customer.getName())
                    .add("address", customer.getAddress())
                    .add("telephone", customer.getTelephone())
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                    .add(CommonConstants.LABEL_MESSAGE, "Failed to retrieve customer: " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to generate the next new unique account number
     *
     * @return
     */
    @Override
    public JsonObject generateNextAccountNumberAsJson() {
        try {
            String nextAccountNumber = generateNextAccountNumber();
            return Json.createObjectBuilder()
                    .add("accountNumber", nextAccountNumber)
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                    .add(CommonConstants.LABEL_MESSAGE, "Failed to generate account number: " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to save or update customer
     *
     * @param accountNumber
     * @param name
     * @param address
     * @param telephone
     * @param status
     * @return
     */
    @Override
    public JsonObject saveOrUpdateCustomer(String accountNumber, String name, String address, String telephone, String status) {
        try {
            // validation
            if (name == null || name.trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, "Customer name is required")
                        .build();
            }

            // set the status as 'A': Active if the event is a new customer creation
            char statusChar = (status != null && !status.isEmpty()) ?
                    status.charAt(0) : CommonConstants.STATUS_ACTIVE_CHAR;

            // generate account number if not provided
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                accountNumber = generateNextAccountNumber();
            }

            Customer customer = new Customer(accountNumber, name, address, telephone, statusChar);

            boolean exists = isExistingCustomer(accountNumber);
            if (exists) {
                updateCustomer(customer);
            } else {
                saveCustomer(customer);
            }

            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_DONE)
                    .add(CommonConstants.LABEL_MESSAGE, exists ?
                            ResponseMessages.MESSAGE_CUSTOMER_UPDATED_SUCCESSFULLY :
                            ResponseMessages.MESSAGE_CUSTOMER_SAVED_SUCCESSFULLY)
                    .add("accountNumber", accountNumber)
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                    .add(CommonConstants.LABEL_MESSAGE, "Failed to save customer: " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to update customer's status
     *
     * @param accountNumber
     * @param status
     * @return
     */
    @Override
    public JsonObject updateCustomerStatus(String accountNumber, String status) {
        try {
            // Validation
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, "Account number is required")
                        .build();
            }

            if (status == null || !status.matches("[AID]")) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.INVALID_REQUEST_PAYLOAD)
                        .build();
            }

            // Check if customer exists
            if (!isExistingCustomer(accountNumber)) {
                return Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, "Customer not found")
                        .build();
            }

            updateStatus(accountNumber, status.charAt(0));

            String actionText = status.equals(CommonConstants.STATUS_ACTIVE_STRING) ? "activated" :
                    status.equals(CommonConstants.STATUS_INACTIVE_STRING) ? "inactivated" :
                            "deleted";

            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_DONE)
                    .add(CommonConstants.LABEL_MESSAGE,
                            ResponseMessages.MESSAGE_CUSTOMER_STATUS_UPDATED.replace(CommonConstants.REPLACER, actionText))
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                    .add(CommonConstants.LABEL_MESSAGE, "Failed to update customer status: " + e.getMessage())
                    .build();
        }
    }

}
