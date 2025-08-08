package com.icbt.pahanaedubookshopjavaee.service;

import com.icbt.pahanaedubookshopjavaee.dto.SupportRequestDTO;

public interface EmailService {
    void sendSupportRequestEmail(SupportRequestDTO supportRequest) throws Exception;
    void sendSupportConfirmationEmail(String userEmail, String ticketId) throws Exception;
}