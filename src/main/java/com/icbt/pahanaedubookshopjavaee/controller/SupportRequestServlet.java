package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.dto.SupportRequestDTO;
import com.icbt.pahanaedubookshopjavaee.service.SupportRequestService;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/support-request")
public class SupportRequestServlet extends BaseServlet {

    private SupportRequestService supportRequestService;

    @Override
    protected void initializeServices() {
        this.supportRequestService = serviceFactory.createSupportRequestService();
    }

    /**
     * This method is used to submit a support request
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject requestJson = reader.readObject();
            
            JsonObject result = supportRequestService.processSupportRequest(requestJson);
            
            if (!result.getBoolean("success", false)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            
            abstractResponseUtility.writeJson(response, result);
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            
            JsonObject errorResponse = Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", ResponseMessages.MESSAGE_INVALID_REQUEST_FORMAT + ": " + e.getMessage())
                    .build();
                    
            abstractResponseUtility.writeJson(response, errorResponse);
        }
    }

    /**
     * This method is used to get a support request
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ticketId = request.getParameter("ticketId");
        
        if (ticketId == null || ticketId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            
            JsonObject errorResponse = Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", ResponseMessages.MESSAGE_TICKET_ID_REQUIRED)
                    .build();
                    
            abstractResponseUtility.writeJson(response, errorResponse);
            return;
        }

        try {
            SupportRequestDTO supportRequest = supportRequestService.getSupportRequest(ticketId);
            
            JsonObject result = Json.createObjectBuilder()
                    .add("success", true)
                    .add("ticketId", supportRequest.getTicketId())
                    .add("issueType", supportRequest.getIssueType())
                    .add("priority", supportRequest.getPriority())
                    .add("subject", supportRequest.getSubject())
                    .add("description", supportRequest.getDescription())
                    .add("userEmail", supportRequest.getUserEmail() != null ? supportRequest.getUserEmail() : "")
                    .add("timestamp", supportRequest.getTimestamp())
                    .add("responseTime", supportRequestService.getResponseTime(supportRequest.getPriority()))
                    .build();
                    
            abstractResponseUtility.writeJson(response, result);
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            
            JsonObject errorResponse = Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", ResponseMessages.MESSAGE_SUPPORT_REQUEST_NOT_FOUND + ": " + e.getMessage())
                    .build();
                    
            abstractResponseUtility.writeJson(response, errorResponse);
        }
    }
    
}