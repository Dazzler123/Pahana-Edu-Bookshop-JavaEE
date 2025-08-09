package com.icbt.pahanaedubookshopjavaee.dao.impl;

import com.icbt.pahanaedubookshopjavaee.dao.SupportRequestDAO;
import com.icbt.pahanaedubookshopjavaee.dto.SupportRequestDTO;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SupportRequestDAOImpl implements SupportRequestDAO {
    
    private final DataSource dataSource;

    public SupportRequestDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This method is used to save the support request
     *
     * @param supportRequest
     * @throws Exception
     */
    @Override
    public void saveSupportRequest(SupportRequestDTO supportRequest) throws Exception {
        String query = "INSERT INTO Support_Request (ticket_id, issue_type, priority, subject, " +
                      "description, user_email, user_agent, timestamp, status) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, supportRequest.getTicketId());
            ps.setString(2, supportRequest.getIssueType());
            ps.setString(3, supportRequest.getPriority());
            ps.setString(4, supportRequest.getSubject());
            ps.setString(5, supportRequest.getDescription());
            ps.setString(6, supportRequest.getUserEmail());
            ps.setString(7, supportRequest.getUserAgent());
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.parse(supportRequest.getTimestamp())));
            ps.setString(9, CommonConstants.SUPPORT_STATUS_OPEN); // Default status
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("Failed to save support request");
            }
            
        } catch (SQLException e) {
            throw new Exception("Database error while saving support request: " + supportRequest.getTicketId(), e);
        }
    }

    /**
     * This method is used to get the support request by ticket id
     *
     * @param ticketId
     * @return
     * @throws Exception
     */
    @Override
    public SupportRequestDTO getSupportRequestByTicketId(String ticketId) throws Exception {
        String query = "SELECT ticket_id, issue_type, priority, subject, description, " +
                      "user_email, user_agent, timestamp, status FROM Support_Request WHERE ticket_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, ticketId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SupportRequestDTO supportRequest = new SupportRequestDTO();
                    supportRequest.setTicketId(rs.getString("ticket_id"));
                    supportRequest.setIssueType(rs.getString("issue_type"));
                    supportRequest.setPriority(rs.getString("priority"));
                    supportRequest.setSubject(rs.getString("subject"));
                    supportRequest.setDescription(rs.getString("description"));
                    supportRequest.setUserEmail(rs.getString("user_email"));
                    supportRequest.setUserAgent(rs.getString("user_agent"));
                    supportRequest.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return supportRequest;
                } else {
                    throw new Exception("Support request not found: " + ticketId);
                }
            }
            
        } catch (SQLException e) {
            throw new Exception("Database error while retrieving support request: " + ticketId, e);
        }
    }

    /**
     * This method is used to update the support request status
     *
     * @param ticketId
     * @param status
     * @throws Exception
     */
    @Override
    public void updateSupportRequestStatus(String ticketId, String status) throws Exception {
        String query = "UPDATE Support_Request SET status = ?, updated_at = ? WHERE ticket_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, ticketId);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("Support request not found: " + ticketId);
            }
            
        } catch (SQLException e) {
            throw new Exception("Database error while updating support request status: " + ticketId, e);
        }
    }

    /**
     * This method is used to check if the ticket exists
     *
     * @param ticketId
     * @return
     * @throws Exception
     */
    @Override
    public boolean ticketExists(String ticketId) throws Exception {
        String query = "SELECT 1 FROM Support_Request WHERE ticket_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, ticketId);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            throw new Exception("Database error while checking ticket existence: " + ticketId, e);
        }
    }

}