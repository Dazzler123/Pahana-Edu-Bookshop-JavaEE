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

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()) {
            PreparedStatement psmt = connection.prepareStatement("SELECT item_code, name, unit_price, qty_on_hand, status FROM Item");
            ResultSet rs = psmt.executeQuery();

            JsonArrayBuilder itemsArray = Json.createArrayBuilder();

            while (rs.next()) {
                JsonObjectBuilder item = Json.createObjectBuilder();
                item.add("itemCode", rs.getString("item_code"));
                item.add("name", rs.getString("name"));
                item.add("unitPrice", rs.getString("unit_price"));
                item.add("qtyOnHand", rs.getString("qty_on_hand"));
                item.add("status", rs.getString("status"));
                itemsArray.add(item);
            }

            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "done");
            response.add("items", itemsArray);
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String itemCode = req.getParameter("item_code");
        String name = req.getParameter("name");
        String unitPrice = req.getParameter("unit_price");
        String qtyOnHand = req.getParameter("qty_on_hand");

        System.out.println(itemCode + " " + name + " " + unitPrice + " " + qtyOnHand);
        resp.setContentType("application/json");

        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()) {
            boolean exists = itemExists(connection, itemCode);

            String sql;
            if (exists) {
                // Preserve existing status
                String currentStatus = getItemStatus(connection, itemCode);
                sql = "UPDATE Item SET name = ?, unit_price = ?, qty_on_hand = ?, status = ? WHERE item_code = ?";
                try (PreparedStatement psmt = connection.prepareStatement(sql)) {
                    psmt.setString(1, name);
                    psmt.setString(2, unitPrice);
                    psmt.setString(3, qtyOnHand);
                    psmt.setString(4, currentStatus); // preserve original
                    psmt.setString(5, itemCode);
                    boolean result = psmt.executeUpdate() > 0;

                    JsonObjectBuilder response = Json.createObjectBuilder();
                    response.add("state", result ? "done" : "error");
                    response.add("message", result ? "Item updated successfully." : "Item update failed!");
                    resp.getWriter().print(response.build());
                }

            } else {
                sql = "INSERT INTO Item (item_code, name, unit_price, qty_on_hand, status) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement psmt = connection.prepareStatement(sql)) {
                    psmt.setString(1, itemCode);
                    psmt.setString(2, name);
                    psmt.setString(3, unitPrice);
                    psmt.setString(4, qtyOnHand);
                    psmt.setString(5, "A"); // New customer = Active
                    boolean result = psmt.executeUpdate() > 0;

                    JsonObjectBuilder response = Json.createObjectBuilder();
                    response.add("state", result ? "done" : "error");
                    response.add("message", result ? "Item created successfully." : "Item creation failed!");
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
     * This method can be used to get the item's status
     * @param connection
     * @param itemCode
     * @return
     * @throws SQLException
     */
    private String getItemStatus(Connection connection, String itemCode) throws SQLException {
        String sql = "SELECT status FROM Item WHERE item_code = ?";
        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.setString(1, itemCode);
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return "I"; // fallback default if somehow not found
    }



    /**
     * This method can be used to find if the item already exists
     *
     * @param connection
     * @param itemCode
     * @return
     * @throws SQLException
     */
    private boolean itemExists(Connection connection, String itemCode) throws SQLException {
        String sql = "SELECT item_code FROM Item WHERE item_code = ?";
        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.setString(1, itemCode);
            return psmt.executeQuery().next(); // true if record found
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject json = Json.createReader(req.getReader()).readObject();
        String itemCode = json.getString("item_code", null);
        String newStatus = json.getString("status", null);

        resp.setContentType("application/json");

        if (itemCode == null || newStatus == null || !newStatus.matches("[AID]")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(Json.createObjectBuilder()
                    .add("state", "error")
                    .add("message", "Invalid request payload.").build());
            return;
        }

        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()) {
            String sql = "UPDATE Item SET status = ? WHERE item_code = ?";
            try (PreparedStatement psmt = connection.prepareStatement(sql)) {
                psmt.setString(1, newStatus);
                psmt.setString(2, itemCode);
                boolean result = psmt.executeUpdate() > 0;

                JsonObjectBuilder response = Json.createObjectBuilder();
                response.add("state", result ? "done" : "error");
                response.add("message", result ? "Status updated successfully." : "Update failed. Item not found.");
                resp.getWriter().print(response.build());
            }

        } catch (SQLException e) {
            JsonObjectBuilder errorObj = Json.createObjectBuilder();
            errorObj.add("state", "error");
            errorObj.add("message", e.getMessage());
            resp.setStatus(500);
            resp.getWriter().print(errorObj.build());
        }
    }

}