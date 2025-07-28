package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.model.Customer;
import com.icbt.pahanaedubookshopjavaee.service.CustomerService;
import com.icbt.pahanaedubookshopjavaee.service.impl.CustomerServiceImpl;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.DBConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {

    private CustomerService customerService;
    private AbstractResponseUtility abstractResponseUtility;

    @Override
    public void init() {
        DataSource dataSource = (DataSource) getServletContext().getAttribute(DBConstants.DBCP_LABEL);
        this.customerService = new CustomerServiceImpl(dataSource);
        this.abstractResponseUtility = new AbstractResponseUtility();
    }

    /**
     * This method is used to get all the available customers
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Customer> customers = customerService.getAllCustomers();

        JsonArrayBuilder customersArray = Json.createArrayBuilder();
        for (Customer c : customers) {
            customersArray.add(Json.createObjectBuilder()
                    .add("accountNumber", c.getAccountNumber())
                    .add("name", c.getName())
                    .add("address", c.getAddress())
                    .add("telephone", c.getTelephone())
                    .add("status", String.valueOf(c.getStatus()))
            );
        }

        JsonObject json = Json.createObjectBuilder()
                .add(CommonConstants.LABEL_STATE, CommonConstants.LABEL_DONE)
                .add("customers", customersArray)
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
        char status = request.getParameter("status") != null ?
                request.getParameter("status").charAt(0) : CommonConstants.STATUS_ACTIVE_CHAR;

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
