package com.icbt.pahanaedubookshopjavaee.controller;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()) {
            PreparedStatement psmt = connection.prepareStatement("SELECT account_number, name, address, telephone, status FROM Customer");
            ResultSet rs = psmt.executeQuery();

            JsonArrayBuilder customersArray = Json.createArrayBuilder();

            while (rs.next()) {
                JsonObjectBuilder customer = Json.createObjectBuilder();
                customer.add("accountNumber", rs.getString("account_number"));
                customer.add("name", rs.getString("name"));
                customer.add("address", rs.getString("address"));
                customer.add("telephone", rs.getString("telephone"));
                customer.add("status", rs.getString("status"));
                customersArray.add(customer);
            }

            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "done");
            response.add("customers", customersArray);
            resp.getWriter().print(response.build());

        } catch (SQLException e) {
            JsonObjectBuilder errorObj = Json.createObjectBuilder();
            errorObj.add("state", "error");
            errorObj.add("message", e.getMessage());
            resp.setStatus(500);
            resp.getWriter().print(errorObj.build());
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountNumber = req.getParameter("account_number");
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        String telephone = req.getParameter("telephone");

        System.out.println(accountNumber + " " + name + " " + address + " " + telephone);
        resp.setContentType("application/json");

        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()) {
            boolean exists = customerExists(connection, accountNumber);

            String sql;
            if (exists) {
                // Preserve existing status
                String currentStatus = getCustomerStatus(connection, accountNumber);
                sql = "UPDATE Customer SET name = ?, address = ?, telephone = ?, status = ? WHERE account_number = ?";
                try (PreparedStatement psmt = connection.prepareStatement(sql)) {
                    psmt.setString(1, name);
                    psmt.setString(2, address);
                    psmt.setString(3, telephone);
                    psmt.setString(4, currentStatus); // preserve original
                    psmt.setString(5, accountNumber);
                    boolean result = psmt.executeUpdate() > 0;

                    JsonObjectBuilder response = Json.createObjectBuilder();
                    response.add("state", result ? "done" : "error");
                    response.add("message", result ? "Customer updated successfully." : "Customer update failed!");
                    resp.getWriter().print(response.build());
                }

            } else {
                sql = "INSERT INTO Customer (account_number, name, address, telephone, status) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement psmt = connection.prepareStatement(sql)) {
                    psmt.setString(1, accountNumber);
                    psmt.setString(2, name);
                    psmt.setString(3, address);
                    psmt.setString(4, telephone);
                    psmt.setString(5, "A"); // New customer = Active
                    boolean result = psmt.executeUpdate() > 0;

                    JsonObjectBuilder response = Json.createObjectBuilder();
                    response.add("state", result ? "done" : "error");
                    response.add("message", result ? "Customer created successfully." : "Customer creation failed!");
                    resp.getWriter().print(response.build());
                }
            }

        } catch (SQLException e) {
            JsonObjectBuilder errorObj = Json.createObjectBuilder();
            errorObj.add("state", "error");
            errorObj.add("message", e.getMessage());
            resp.setStatus(400);
            resp.getWriter().print(errorObj.build());
        }
    }

    /**
     * This method can be used to get the customer's status
     * @param connection
     * @param accountNumber
     * @return
     * @throws SQLException
     */
    private String getCustomerStatus(Connection connection, String accountNumber) throws SQLException {
        String sql = "SELECT status FROM Customer WHERE account_number = ?";
        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.setString(1, accountNumber);
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return "I"; // fallback default if somehow not found
    }


    /**
     * This method can be used to find if the customer already exists
     *
     * @param connection
     * @param accountNumber
     * @return
     * @throws SQLException
     */
    private boolean customerExists(Connection connection, String accountNumber) throws SQLException {
        String sql = "SELECT account_number FROM Customer WHERE account_number = ?";
        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.setString(1, accountNumber);
            return psmt.executeQuery().next(); // true if record found
        }
    }



    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
