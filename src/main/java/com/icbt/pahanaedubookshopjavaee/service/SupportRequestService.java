package com.icbt.pahanaedubookshopjavaee.service;

import com.icbt.pahanaedubookshopjavaee.dto.SupportRequestDTO;

import javax.json.JsonObject;

public interface SupportRequestService {
    void saveSupportRequest(SupportRequestDTO supportRequest) throws Exception;

    SupportRequestDTO getSupportRequest(String ticketId) throws Exception;

    void updateSupportRequestStatus(String ticketId, String status) throws Exception;

    JsonObject processSupportRequest(JsonObject requestJson);

    String generateTicketId();

    String getResponseTime(String priority);

    boolean isValidPriority(String priority);

    boolean isValidIssueType(String issueType);
}