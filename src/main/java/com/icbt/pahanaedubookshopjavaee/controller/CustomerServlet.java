package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.service.CustomerService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;

@WebServlet("/customer")
public class CustomerServlet extends BaseServlet {

    private CustomerService customerService;

    @Override
    protected void initializeServices() {
        this.customerService = serviceFactory.createCustomerService();
    }

    /**
     * This method is used to get all the available customers, or generate a new account number.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String accountNumber = request.getParameter("accountNumber");

        JsonObject result;

        if ("ids".equals(action)) {
            result = customerService.getAllCustomerIdsAsJson();
        } else if ("generateAccountNumber".equals(action)) {
            result = customerService.generateNextAccountNumberAsJson();
        } else if (accountNumber != null && !accountNumber.isEmpty()) {
            result = customerService.getCustomerByAccountNumberAsJson(accountNumber);
            if (result.containsKey("state") && "error".equals(result.getString("state"))) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            result = customerService.getAllCustomersAsJson();
        }

        abstractResponseUtility.writeJson(response, result);
    }

    /**
     * This method is used to save or update a customer
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accountNumber = request.getParameter("account_number");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String telephone = request.getParameter("telephone");
        String status = request.getParameter("status");

        JsonObject result = customerService.saveOrUpdateCustomer(accountNumber, name, address, telephone, status);
        abstractResponseUtility.writeJson(response, result);
    }


    /**
     * This method is used to update the customer's status
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject json = reader.readObject();
            String accountNumber = json.getString("account_number", null);
            String status = json.getString("status", null);

            JsonObject result = customerService.updateCustomerStatus(accountNumber, status);

            if (result.containsKey("state") && "error".equals(result.getString("state"))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            abstractResponseUtility.writeJson(response, result);
        }
    }

}