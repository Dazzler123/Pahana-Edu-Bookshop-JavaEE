package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.model.Customer;
import com.icbt.pahanaedubookshopjavaee.service.CustomerService;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

@WebServlet("/customer")
public class CustomerServlet extends BaseServlet {

    private CustomerService customerService;

    @Override
    protected void initializeServices() {
        this.customerService = serviceFactory.createCustomerService();
    }

    /**
     * This method is used to get all the available customers
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String accountNumber = request.getParameter("accountNumber");

        // this is to load all customer's ids
        if ("ids".equals(action)) {
            List<String> ids = customerService.getAllCustomerIds();
            JsonArrayBuilder idArray = Json.createArrayBuilder();
            ids.forEach(id -> idArray.add(id));

            JsonObject json = Json.createObjectBuilder()
                    .add("customerIds", idArray)
                    .build();
            abstractResponseUtility.writeJson(response, json);
            return;
        }

        // this is to generate next account number
        if ("generateAccountNumber".equals(action)) {
            String nextAccountNumber = customerService.generateNextAccountNumber();
            JsonObject json = Json.createObjectBuilder()
                    .add("accountNumber", nextAccountNumber)
                    .build();
            abstractResponseUtility.writeJson(response, json);
            return;
        }

        // this is for select customer operations
        if (accountNumber != null && !accountNumber.isEmpty()) {
            Customer customer = customerService.getCustomerById(accountNumber);
            if (customer == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                        .add("message", "Customer not found")
                        .build());
                return;
            }

            JsonObject json = Json.createObjectBuilder()
                    .add("accountNumber", customer.getAccountNumber())
                    .add("name", customer.getName())
                    .add("address", customer.getAddress())
                    .add("telephone", customer.getTelephone())
                    .build();
            abstractResponseUtility.writeJson(response, json);
            return;
        }

        List<Customer> customers = customerService.getAllCustomers();
        JsonArrayBuilder customerArray = Json.createArrayBuilder();
        for (Customer c : customers) {
            customerArray.add(Json.createObjectBuilder()
                    .add("accountNumber", c.getAccountNumber())
                    .add("name", c.getName())
                    .add("address", c.getAddress())
                    .add("telephone", c.getTelephone())
                    .add("status", String.valueOf(c.getStatus())));
        }

        JsonObject json = Json.createObjectBuilder()
                .add("customers", customerArray)
                .build();
        abstractResponseUtility.writeJson(response, json);
    }


    /**
     * This method is used to create/save new customer
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String accountNumber = request.getParameter("account_number");
        String name = request.getParameter("name");
        String addr = request.getParameter("address");
        String tel = request.getParameter("telephone");

        String statusParam = request.getParameter("status");
        char status = (statusParam != null && !statusParam.isEmpty()) ?
                statusParam.charAt(0) : CommonConstants.STATUS_ACTIVE_CHAR;

        Customer customer = new Customer(accountNumber, name, addr, tel, status);

        boolean exists = customerService.isExistingCustomer(accountNumber);
        if (exists) {
            customerService.updateCustomer(customer);
        } else {
            customerService.saveCustomer(customer);
        }

        JsonObject json = Json.createObjectBuilder()
                .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_DONE)
                .add(CommonConstants.LABEL_MESSAGE, exists ? ResponseMessages.MESSAGE_CUSTOMER_UPDATED_SUCCESSFULLY :
                        ResponseMessages.MESSAGE_CUSTOMER_SAVED_SUCCESSFULLY)
                .build();

        abstractResponseUtility.writeJson(response, json);
    }


    /**
     * This method is used to update the customer's status
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject json = reader.readObject();
            String acc = json.getString("account_number", null);
            String stat = json.getString("status", null);

            if (acc == null || stat == null || !stat.matches("[AID]")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                        .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_ERROR)
                        .add(CommonConstants.LABEL_MESSAGE, ResponseMessages.INVALID_REQUEST_PAYLOAD)
                        .build());
                return;
            }

            customerService.updateStatus(acc, stat.charAt(0));

            String actionText = stat.equals(CommonConstants.STATUS_ACTIVE_STRING) ? "activated" :
                    stat.equals(CommonConstants.STATUS_INACTIVE_STRING) ? "inactivated" :
                            "deleted";

            abstractResponseUtility.writeJson(response, Json.createObjectBuilder()
                    .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_DONE)
                    .add(CommonConstants.LABEL_MESSAGE,
                            ResponseMessages.MESSAGE_CUSTOMER_STATUS_UPDATED.replace(CommonConstants.REPLACER, actionText))
                    .build());
        }
    }

}
