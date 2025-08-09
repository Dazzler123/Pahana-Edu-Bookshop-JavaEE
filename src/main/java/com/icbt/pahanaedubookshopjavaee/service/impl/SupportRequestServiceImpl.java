package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.SupportRequestDAO;
import com.icbt.pahanaedubookshopjavaee.dao.impl.SupportRequestDAOImpl;
import com.icbt.pahanaedubookshopjavaee.dto.SupportRequestDTO;
import com.icbt.pahanaedubookshopjavaee.service.EmailService;
import com.icbt.pahanaedubookshopjavaee.service.SupportRequestService;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;
import com.icbt.pahanaedubookshopjavaee.util.constants.ExceptionMessages;

import javax.json.Json;
import javax.json.JsonObject;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class SupportRequestServiceImpl implements SupportRequestService {
    
    private final SupportRequestDAO supportRequestDAO;
    private final EmailService emailService;
    
    private static final List<String> VALID_PRIORITIES = Arrays.asList(
        CommonConstants.PRIORITY_LOW, 
        CommonConstants.PRIORITY_MEDIUM, 
        CommonConstants.PRIORITY_HIGH, 
        CommonConstants.PRIORITY_URGENT
    );
    
    private static final List<String> VALID_ISSUE_TYPES = Arrays.asList(
        CommonConstants.ISSUE_TYPE_TECHNICAL, 
        CommonConstants.ISSUE_TYPE_BILLING, 
        CommonConstants.ISSUE_TYPE_ACCOUNT, 
        CommonConstants.ISSUE_TYPE_ORDER, 
        CommonConstants.ISSUE_TYPE_PRODUCT, 
        CommonConstants.ISSUE_TYPE_GENERAL, 
        CommonConstants.ISSUE_TYPE_BUG, 
        CommonConstants.ISSUE_TYPE_FEATURE
    );

    public SupportRequestServiceImpl(DataSource dataSource, EmailService emailService) {
        this.supportRequestDAO = new SupportRequestDAOImpl(dataSource);
        this.emailService = emailService;
    }

    @Override
    public void saveSupportRequest(SupportRequestDTO supportRequest) throws Exception {
        supportRequestDAO.saveSupportRequest(supportRequest);
    }

    @Override
    public SupportRequestDTO getSupportRequest(String ticketId) throws Exception {
        return supportRequestDAO.getSupportRequestByTicketId(ticketId);
    }

    @Override
    public void updateSupportRequestStatus(String ticketId, String status) throws Exception {
        supportRequestDAO.updateSupportRequestStatus(ticketId, status);
    }

    /**
     * This method is used to process the support request
     *
     * @param requestJson
     * @return
     */
    @Override
    public JsonObject processSupportRequest(JsonObject requestJson) {
        try {
            // Validate required fields
            JsonObject validationResult = validateSupportRequest(requestJson);
            if (validationResult != null) {
                return validationResult;
            }

            // Generate unique ticket ID
            String ticketId = generateTicketId();

            // Create support request DTO
            SupportRequestDTO supportRequest = new SupportRequestDTO();
            supportRequest.setTicketId(ticketId);
            supportRequest.setIssueType(requestJson.getString("issueType", CommonConstants.ISSUE_TYPE_GENERAL));
            supportRequest.setPriority(requestJson.getString("priority", CommonConstants.PRIORITY_MEDIUM));
            supportRequest.setSubject(requestJson.getString("subject", ""));
            supportRequest.setDescription(requestJson.getString("description", ""));
            supportRequest.setUserEmail(requestJson.getString("userEmail", null));
            supportRequest.setUserAgent(requestJson.getString("userAgent", ""));
            supportRequest.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // Save to database
            saveSupportRequest(supportRequest);

            // Send email to support team
            emailService.sendSupportRequestEmail(supportRequest);

            // Send confirmation email to user if email provided
            if (supportRequest.getUserEmail() != null && !supportRequest.getUserEmail().trim().isEmpty()) {
                emailService.sendSupportConfirmationEmail(supportRequest.getUserEmail(), ticketId);
            }

            // Return success response
            return Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", ResponseMessages.MESSAGE_SUPPORT_REQUEST_SUBMITTED_SUCCESSFULLY)
                    .add("ticketId", ticketId)
                    .add("responseTime", getResponseTime(supportRequest.getPriority()))
                    .add("status", CommonConstants.SUPPORT_STATUS_OPEN)
                    .build();

        } catch (Exception e) {
            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", ExceptionMessages.FAILED_TO_SUBMIT_SUPPORT_REQUEST + ": " + e.getMessage())
                    .build();
        }
    }

    /**
     * This method is used to generate a unique ticket ID
     *
     * @return
     */
    @Override
    public String generateTicketId() {
        return "TKT-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    /**
     * This method is used to get the response time based on the priority
     *
     * @param priority
     * @return
     */
    @Override
    public String getResponseTime(String priority) {
        if (priority == null) {
            return "24-48 hours";
        }
        
        switch (priority.toUpperCase()) {
            case CommonConstants.PRIORITY_URGENT:
                return "1-2 hours";
            case CommonConstants.PRIORITY_HIGH:
                return "2-4 hours";
            case CommonConstants.PRIORITY_MEDIUM:
                return "24 hours";
            case CommonConstants.PRIORITY_LOW:
                return "48-72 hours";
            default:
                return "24-48 hours";
        }
    }

    /**
     * This method is used to validate the priority
     *
     * @param priority
     * @return
     */
    @Override
    public boolean isValidPriority(String priority) {
        return priority != null && VALID_PRIORITIES.contains(priority.toUpperCase());
    }

    /**
     * This method is used to validate the issue type
     *
     * @param issueType
     * @return
     */
    @Override
    public boolean isValidIssueType(String issueType) {
        return issueType != null && VALID_ISSUE_TYPES.contains(issueType.toUpperCase());
    }

    /**
     * This method is used to validate the support request
     *
     * @param requestJson
     * @return
     */
    private JsonObject validateSupportRequest(JsonObject requestJson) {
        // Validate subject
        if (!requestJson.containsKey("subject") || requestJson.getString("subject").trim().isEmpty()) {
            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", ResponseMessages.MESSAGE_SUBJECT_REQUIRED)
                    .build();
        }

        String subject = requestJson.getString("subject");
        if (subject.length() > 200) {
            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", ResponseMessages.MESSAGE_SUBJECT_TOO_LONG)
                    .build();
        }

        // Validate description
        if (!requestJson.containsKey("description") || requestJson.getString("description").trim().isEmpty()) {
            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", ResponseMessages.MESSAGE_DESCRIPTION_REQUIRED)
                    .build();
        }

        String description = requestJson.getString("description");
        if (description.length() > 2000) {
            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", ResponseMessages.MESSAGE_DESCRIPTION_TOO_LONG)
                    .build();
        }

        // Validate priority
        String priority = requestJson.getString("priority", CommonConstants.PRIORITY_MEDIUM);
        if (!isValidPriority(priority)) {
            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", ResponseMessages.MESSAGE_INVALID_PRIORITY)
                    .build();
        }

        // Validate issue type
        String issueType = requestJson.getString("issueType", CommonConstants.ISSUE_TYPE_GENERAL);
        if (!isValidIssueType(issueType)) {
            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", ResponseMessages.MESSAGE_INVALID_ISSUE_TYPE)
                    .build();
        }

        // Validate email format if provided
        String userEmail = requestJson.getString("userEmail", null);
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            if (!isValidEmail(userEmail)) {
                return Json.createObjectBuilder()
                        .add("success", false)
                        .add("message", ResponseMessages.MESSAGE_INVALID_EMAIL_FORMAT)
                        .build();
            }
        }

        return null; // No validation errors
    }

    /**
     * This method is used to validate the email format
     *
     * @param email
     * @return
     */
    private boolean isValidEmail(String email) {
        return email != null && 
               email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$") &&
               email.length() <= 100;
    }

}