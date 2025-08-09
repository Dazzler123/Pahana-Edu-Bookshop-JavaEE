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

    // =========== PLACE ORDER RESPONSE MESSAGES =========== -ST
    public static final String MESSAGE_ORDER_PLACED_SUCCESSFULLY = "Order placed successfully. Order Code: " + CommonConstants.REPLACER;
    public static final String MESSAGE_CUSTOMER_ACCOUNT_REQUIRED = "Customer account is required";
    public static final String MESSAGE_PAYMENT_METHOD_REQUIRED = "Payment method is required";
    public static final String MESSAGE_ORDER_ITEMS_REQUIRED = "Order must contain at least one item";
    public static final String MESSAGE_INVALID_PAYMENT_METHOD = "Invalid payment method. Allowed: Cash, Card, Other";
    public static final String MESSAGE_ORDER_TOTAL_MUST_BE_POSITIVE = "Order total must be greater than zero";
    public static final String MESSAGE_DISCOUNT_PERCENTAGE_INVALID = "Discount percentage must be between 0 and 100 for item: " + CommonConstants.REPLACER;
    public static final String MESSAGE_INVALID_LINE_TOTAL_CALCULATION = "Invalid line total calculation for item: " + CommonConstants.REPLACER;
    public static final String MESSAGE_INSUFFICIENT_STOCK = "Insufficient stock for item: " + CommonConstants.REPLACER;
    
    // Order Item Validation Messages
    public static final String MESSAGE_ITEM_CODE_REQUIRED_FOR_ORDER = "Item code is required for order item";
    public static final String MESSAGE_QUANTITY_REQUIRED_FOR_ORDER = "Quantity is required for item: " + CommonConstants.REPLACER;
    public static final String MESSAGE_UNIT_PRICE_REQUIRED_FOR_ORDER = "Unit price is required for item: " + CommonConstants.REPLACER;
    public static final String MESSAGE_DISCOUNT_REQUIRED_FOR_ORDER = "Discount is required for item: " + CommonConstants.REPLACER;
    public static final String MESSAGE_QUANTITY_MUST_BE_POSITIVE = "Quantity must be greater than zero for item: " + CommonConstants.REPLACER;
    public static final String MESSAGE_UNIT_PRICE_MUST_BE_POSITIVE_FOR_ORDER = "Unit price must be greater than zero for item: " + CommonConstants.REPLACER;
    public static final String MESSAGE_INVALID_NUMERIC_VALUE_IN_ITEM = "Invalid numeric value in item data";
    public static final String MESSAGE_INVALID_ITEM_DATA_FORMAT = "Invalid item data format";
    // =========== PLACE ORDER RESPONSE MESSAGES =========== -ED

    // =========== MANAGE ORDER RESPONSE MESSAGES =========== -ST
    public static final String MESSAGE_ORDER_UPDATED_SUCCESSFULLY = "Order updated successfully";
    public static final String MESSAGE_ORDER_STATUS_UPDATED_SUCCESSFULLY = "Order " + CommonConstants.REPLACER + " successfully";
    public static final String MESSAGE_ORDER_NOT_FOUND = "Order not found";
    public static final String MESSAGE_ORDER_CODE_REQUIRED = "Order code is required";
    public static final String MESSAGE_ORDER_DATE_REQUIRED = "Order date is required";
    public static final String MESSAGE_TOTAL_AMOUNT_REQUIRED = "Total amount is required";
    public static final String MESSAGE_TOTAL_DISCOUNT_REQUIRED = "Total discount is required";
    public static final String MESSAGE_STATUS_REQUIRED = "Status is required";
    public static final String MESSAGE_TOTAL_AMOUNT_CANNOT_BE_NEGATIVE = "Total amount cannot be negative";
    public static final String MESSAGE_TOTAL_DISCOUNT_CANNOT_BE_NEGATIVE = "Total discount cannot be negative";
    public static final String MESSAGE_DISCOUNT_CANNOT_EXCEED_TOTAL = "Total discount cannot exceed total amount";
    public static final String MESSAGE_INVALID_ORDER_DATE_FORMAT = "Invalid order date format";
    public static final String MESSAGE_INVALID_ORDER_STATUS = "Invalid order status. Must be A (Active), I (Inactive), or D (Deleted)";
    public static final String MESSAGE_INVALID_PAYMENT_STATUS = "Invalid payment status. Must be P (Paid), N (Not Paid), or R (Refunded)";
    public static final String MESSAGE_INVALID_PAYMENT_TYPE = "Invalid payment type. Must be cash, card, or other";
    public static final String MESSAGE_INVALID_NUMERIC_VALUE_IN_ORDER = "Invalid numeric value in order data";
    public static final String MESSAGE_FAILED_TO_LOAD_ORDERS = "Failed to load orders";
    // =========== MANAGE ORDER RESPONSE MESSAGES =========== -ED

}
