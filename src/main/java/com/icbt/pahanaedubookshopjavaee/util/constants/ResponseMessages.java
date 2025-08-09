package com.icbt.pahanaedubookshopjavaee.util.constants;

public class ResponseMessages {

    // =========== CUSTOMER RESPONSE MESSAGES =========== -ST
    public static final String MESSAGE_CUSTOMER_SAVED_SUCCESSFULLY = "Customer saved successfully.";
    public static final String MESSAGE_CUSTOMER_UPDATED_SUCCESSFULLY = "Customer updated successfully.";
    public static final String MESSAGE_CUSTOMER_STATUS_UPDATED = "Customer " + CommonConstants.REPLACER + " successfully.";
    public static final String MESSAGE_CUSTOMER_NOT_FOUND = "Customer not found";
    public static final String MESSAGE_CUSTOMER_NAME_REQUIRED = "Customer name is required";
    public static final String MESSAGE_CUSTOMER_NAME_TOO_LONG = "Customer name cannot exceed 100 characters";
    public static final String MESSAGE_CUSTOMER_ADDRESS_TOO_LONG = "Customer address cannot exceed 200 characters";
    public static final String MESSAGE_CUSTOMER_TELEPHONE_TOO_LONG = "Customer telephone cannot exceed 20 characters";
    public static final String MESSAGE_INVALID_CUSTOMER_STATUS = "Invalid customer status. Must be one of: A (Active), I (Inactive), D (Deleted)";
    public static final String MESSAGE_ACCOUNT_NUMBER_REQUIRED = "Account number is required";
    public static final String MESSAGE_FAILED_TO_GENERATE_ACCOUNT_NUMBER = "Failed to generate account number";
    public static final String MESSAGE_FAILED_TO_RETRIEVE_CUSTOMERS = "Failed to retrieve customers";
    // =========== CUSTOMER RESPONSE MESSAGES =========== -ED

    // =========== ITEM RESPONSE MESSAGES =========== -ST
    public static final String MESSAGE_ITEM_SAVED_SUCCESSFULLY = "Item saved successfully.";
    public static final String MESSAGE_ITEM_UPDATED_SUCCESSFULLY = "Item updated successfully.";
    public static final String MESSAGE_ITEM_STATUS_UPDATED = "Item " + CommonConstants.REPLACER + " successfully.";
    public static final String MESSAGE_ITEM_NOT_FOUND = "Item not found";
    public static final String MESSAGE_ITEM_NAME_REQUIRED = "Item name is required";
    public static final String MESSAGE_ITEM_NAME_TOO_LONG = "Item name cannot exceed 100 characters";
    public static final String MESSAGE_UNIT_PRICE_REQUIRED = "Unit price is required";
    public static final String MESSAGE_UNIT_PRICE_MUST_BE_POSITIVE = "Unit price must be positive";
    public static final String MESSAGE_INVALID_UNIT_PRICE_FORMAT = "Invalid unit price format";
    public static final String MESSAGE_QTY_ON_HAND_REQUIRED = "Quantity on hand is required";
    public static final String MESSAGE_QTY_ON_HAND_CANNOT_BE_NEGATIVE = "Quantity on hand cannot be negative";
    public static final String MESSAGE_INVALID_QUANTITY_FORMAT = "Invalid quantity format";
    public static final String MESSAGE_INVALID_ITEM_STATUS = "Invalid item status. Must be one of: A (Active), I (Inactive), D (Deleted)";
    public static final String MESSAGE_ITEM_CODE_REQUIRED = "Item code is required";
    public static final String MESSAGE_FAILED_TO_GENERATE_ITEM_CODE = "Failed to generate item code";
    public static final String MESSAGE_FAILED_TO_RETRIEVE_ITEMS = "Failed to retrieve items";
    public static final String MESSAGE_FAILED_TO_SAVE_ITEM = "Failed to save item";
    // =========== ITEM RESPONSE MESSAGES =========== -ED

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
