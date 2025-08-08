package com.icbt.pahanaedubookshopjavaee.dao.impl;

import com.icbt.pahanaedubookshopjavaee.dao.ItemDAO;
import com.icbt.pahanaedubookshopjavaee.model.Item;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ExceptionMessages;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAOImpl implements ItemDAO {

    private final DataSource dataSource;

    public ItemDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This method will return all the available items in the database
     *
     * @return
     */
    @Override
    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT item_code, name, unit_price, qty_on_hand, status FROM Item";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()) {

            while (rs.next()) {
                Item item = new Item(
                        rs.getString("item_code"),
                        rs.getString("name"),
                        rs.getBigDecimal("unit_price"),
                        rs.getInt("qty_on_hand"),
                        rs.getString("status").charAt(0)
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_LOAD_ALL_ITEMS, e);
        }

        return items;
    }

    /**
     * This method will save the provided item
     *
     * @param item
     */
    @Override
    public void save(Item item) {
        String sql = "INSERT INTO Item (item_code, name, unit_price, qty_on_hand, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setString(1, item.getItemCode());
            psmt.setString(2, item.getName());
            psmt.setBigDecimal(3, item.getUnitPrice());
            psmt.setInt(4, item.getQtyOnHand());
            psmt.setString(5, String.valueOf(item.getStatus()));
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_SAVE_ITEM, e);
        }
    }

    /**
     * This method will update the provided item
     *
     * @param item
     */
    @Override
    public void update(Item item) {
        String sql = "UPDATE Item SET name = ?, unit_price = ?, qty_on_hand = ?, status = ? WHERE item_code = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setString(1, item.getName());
            psmt.setBigDecimal(2, item.getUnitPrice());
            psmt.setInt(3, item.getQtyOnHand());
            psmt.setString(4, String.valueOf(item.getStatus()));
            psmt.setString(5, item.getItemCode());
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_UPDATE_ITEM, e);
        }
    }

    /**
     * This method will return the status if the item is available or not
     *
     * @param itemCode
     * @return
     */
    @Override
    public boolean exists(String itemCode) {
        String sql = "SELECT 1 FROM Item WHERE item_code = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setString(1, itemCode);
            try (ResultSet rs = psmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_FIND_ITEM, e);
        }
    }

    /**
     * This method will update the status of the provided item
     *
     * @param itemCode
     * @param status
     */
    @Override
    public void updateStatus(String itemCode, char status) {
        String sql = "UPDATE Item SET status = ? WHERE item_code = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setString(1, String.valueOf(status));
            psmt.setString(2, itemCode);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_UPDATE_ITEM_STATUS, e);
        }
    }

    /**
     * This method will return the status of the provided item
     * @param itemCode
     * @return
     */
    @Override
    public char getStatus(String itemCode) {
        String sql = "SELECT status FROM Item WHERE item_code = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setString(1, itemCode);
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status").charAt(0);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(ExceptionMessages.FAILED_TO_GET_ITEM_STATUS, e);
        }
        return CommonConstants.STATUS_INACTIVE_CHAR;
    }

    /**
     * This method generates the next unique item code
     */
    @Override
    public String generateNextItemCode() {
        String sql = "SELECT item_code FROM Item WHERE item_code LIKE 'ITM-%' ORDER BY item_code DESC LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()) {

            if (rs.next()) {
                String lastItemCode = rs.getString("item_code");
                int lastNumber = Integer.parseInt(lastItemCode.substring(4)); // Remove "ITM-" prefix
                return "ITM-" + String.format("%04d", lastNumber + 1);
            } else {
                return "ITM-0001"; // First item
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate item code", e);
        }
    }
}
