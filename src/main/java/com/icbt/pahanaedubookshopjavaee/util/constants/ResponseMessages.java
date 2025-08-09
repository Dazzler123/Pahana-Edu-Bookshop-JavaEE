package com.icbt.pahanaedubookshopjavaee.util.constants;

public class ResponseMessages {

    // =========== RESPONSE MESSAGES =========== -ST
    public static final String MESSAGE_CUSTOMER_SAVED_SUCCESSFULLY = "Customer saved successfully.";
    public static final String MESSAGE_CUSTOMER_UPDATED_SUCCESSFULLY = "Customer updated successfully.";
    public static final String MESSAGE_ITEM_SAVED_SUCCESSFULLY = "Item saved successfully.";
    public static final String MESSAGE_ITEM_UPDATED_SUCCESSFULLY = "Item updated successfully.";
    public static final String MESSAGE_CUSTOMER_STATUS_UPDATED = "Customer " + CommonConstants.REPLACER + " successfully.";
    public static final String MESSAGE_ITEM_STATUS_UPDATED = "Item " + CommonConstants.REPLACER + " successfully.";
    // =========== RESPONSE MESSAGES =========== -ED

    public static final String INVALID_REQUEST_PAYLOAD = "Invalid request payload";

    // =========== SUPPORT REQUEST RESPONSE MESSAGES =========== -ST
    public static final String MESSAGE_SUPPORT_REQUEST_SUBMITTED_SUCCESSFULLY = "Support request submitted successfully!";
    public static final String MESSAGE_SUPPORT_REQUEST_NOT_FOUND = "Support request not found";
    public static final String MESSAGE_SUPPORT_REQUEST_STATUS_UPDATED = "Support request status updated successfully";
    // =========== SUPPORT REQUEST RESPONSE MESSAGES =========== -ED

    // Validation Messages
    public static final String MESSAGE_SUBJECT_REQUIRED = "Subject is required";
    public static final String MESSAGE_SUBJECT_TOO_LONG = "Subject cannot exceed 200 characters";
    public static final String MESSAGE_DESCRIPTION_REQUIRED = "Description is required";
    public static final String MESSAGE_DESCRIPTION_TOO_LONG = "Description cannot exceed 2000 characters";
    public static final String MESSAGE_INVALID_PRIORITY = "Invalid priority. Must be one of: LOW, MEDIUM, HIGH, URGENT";
    public static final String MESSAGE_INVALID_ISSUE_TYPE = "Invalid issue type. Must be one of: TECHNICAL, BILLING, ACCOUNT, ORDER, PRODUCT, GENERAL, BUG, FEATURE";
    public static final String MESSAGE_INVALID_EMAIL_FORMAT = "Invalid email format";
    public static final String MESSAGE_TICKET_ID_REQUIRED = "Ticket ID is required";
    public static final String MESSAGE_INVALID_REQUEST_FORMAT = "Invalid request format";

}
