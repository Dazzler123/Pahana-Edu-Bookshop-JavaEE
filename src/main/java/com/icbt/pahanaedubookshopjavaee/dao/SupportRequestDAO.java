package com.icbt.pahanaedubookshopjavaee.dao;

import com.icbt.pahanaedubookshopjavaee.dto.SupportRequestDTO;

public interface SupportRequestDAO {
    void saveSupportRequest(SupportRequestDTO supportRequest) throws Exception;

    SupportRequestDTO getSupportRequestByTicketId(String ticketId) throws Exception;

    void updateSupportRequestStatus(String ticketId, String status) throws Exception;

    boolean ticketExists(String ticketId) throws Exception;
}