package com.icbt.pahanaedubookshopjavaee.dao.impl;

import com.icbt.pahanaedubookshopjavaee.dao.CustomerDAO;
import com.icbt.pahanaedubookshopjavaee.model.Customer;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ExceptionMessages;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {
    private final DataSource dataSource;

    public CustomerDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This method will return all the available customers in the database
     *
     */
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT account_number, name, address, telephone, status FROM Customer";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("account_number"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("telephone"),
                        rs.getString("status").charAt(0)
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_LOAD_ALL_CUSTOMERS, e);
        }

        return customers;
    }

    /**
     * This method will save the provided customer
     *
     * @param c
     */
    public void save(Customer c) {
        String sql = "INSERT INTO Customer (account_number, name, address, telephone, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setString(1, c.getAccountNumber());
            psmt.setString(2, c.getName());
            psmt.setString(3, c.getAddress());
            psmt.setString(4, c.getTelephone());
            psmt.setString(5, String.valueOf(c.getStatus()));
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_SAVE_CUSTOMER, e);
        }
    }

    /**
     * This method will update the provided customer
     *
     * @param c
     */
    public void update(Customer c) {
        String sql = "UPDATE Customer SET name = ?, address = ?, telephone = ?, status = ? WHERE account_number = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setString(1, c.getName());
            psmt.setString(2, c.getAddress());
            psmt.setString(3, c.getTelephone());
            psmt.setString(4, String.valueOf(c.getStatus()));
            psmt.setString(5, c.getAccountNumber());
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_UPDATE_CUSTOMER, e);
        }
    }

    /**
     * This method will return a boolean which contains the status
     * of which is the customer is available or not.
     *
     * @param accountNumber
     * @return
     */
    public boolean exists(String accountNumber) {
        String sql = "SELECT 1 FROM Customer WHERE account_number = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setString(1, accountNumber);
            try (ResultSet rs = psmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_FIND_CUSTOMER, e);
        }
    }

    /**
     * This method will update the status of the customer
     *
     * @param acc
     * @param status
     */
    public void updateStatus(String acc, char status) {
        String sql = "UPDATE Customer SET status = ? WHERE account_number = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setString(1, String.valueOf(status));
            psmt.setString(2, acc);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_UPDATE_CUSTOMER_STATUS, e);
        }
    }

    /**
     * This method will give the status of the provided customer
     *
     * @param accountNumber
     * @return
     */
    public char getStatus(String accountNumber) {
        String sql = "SELECT status FROM Customer WHERE account_number = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setString(1, accountNumber);
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status").charAt(0);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_GET_CUSTOMER_STATUS, e);
        }
        return CommonConstants.STATUS_INACTIVE_CHAR;
    }

}
