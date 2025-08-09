package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.dto.SupportRequestDTO;
import com.icbt.pahanaedubookshopjavaee.service.EmailService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@WebServlet("/support-request")
public class SupportRequestServlet extends BaseStatelessServlet {

    private EmailService emailService;

    @Override
    protected void initializeServices() {
        this.emailService = serviceFactory.createEmailService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject json = reader.readObject();

            // Generate unique ticket ID
            String ticketId = "TKT-" + System.currentTimeMillis();

            // Create support request DTO
            SupportRequestDTO supportRequest = new SupportRequestDTO();
            supportRequest.setTicketId(ticketId);
            supportRequest.setIssueType(json.getString("issueType", ""));
            supportRequest.setPriority(json.getString("priority", "medium"));
            supportRequest.setSubject(json.getString("subject", ""));
            supportRequest.setDescription(json.getString("description", ""));
            supportRequest.setUserEmail(json.getString("userEmail", null));
            supportRequest.setUserAgent(json.getString("userAgent", ""));
            supportRequest.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // Send email to support team
            emailService.sendSupportRequestEmail(supportRequest);

            // Send confirmation email to user if email provided
            if (supportRequest.getUserEmail() != null && !supportRequest.getUserEmail().trim().isEmpty()) {
                emailService.sendSupportConfirmationEmail(supportRequest.getUserEmail(), ticketId);
            }

            // Return success response
            JsonObject responseJson = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Support request submitted successfully!")
                    .add("ticketId", ticketId)
                    .add("responseTime", getResponseTime(supportRequest.getPriority()))
                    .build();

            abstractResponseUtility.writeJson(response, responseJson);

        } catch (Exception e) {
            e.printStackTrace();

            JsonObject errorResponse = Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", "Failed to submit support request: " + e.getMessage())
                    .build();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            abstractResponseUtility.writeJson(response, errorResponse);
        }
    }

    private String getResponseTime(String priority) {
        switch (priority.toLowerCase()) {
            case "urgent":
            case "high":
                return "2-4 hours";
            case "medium":
                return "24 hours";
            case "low":
                return "48-72 hours";
            default:
                return "24-48 hours";
        }
    }
}