package com.icbt.pahanaedubookshopjavaee.util.constants;

public class ExceptionMessages {
    // =========== CUSTOMER EXCEPTION MESSAGES =========== -ST
    public static final String FAILED_TO_LOAD_ALL_CUSTOMERS = "Failed to load all customers.";
    public static final String FAILED_TO_SAVE_CUSTOMER = "Failed to save customer.";
    public static final String FAILED_TO_UPDATE_CUSTOMER = "Failed to update customer.";
    public static final String FAILED_TO_FIND_CUSTOMER = "Failed to check if customer exists.";
    public static final String FAILED_TO_UPDATE_CUSTOMER_STATUS = "Failed to update customer status.";
    public static final String FAILED_TO_GET_CUSTOMER_STATUS = "Failed to get customer status.";
    public static final String FAILED_TO_GENERATE_CUSTOMER_ACCOUNT_NUMBER = "Failed to generate customer account number.";
    public static final String FAILED_TO_RETRIEVE_CUSTOMER_BY_ID = "Failed to retrieve customer by ID.";
    public static final String FAILED_TO_RETRIEVE_CUSTOMER_IDS = "Failed to retrieve customer IDs.";
    public static final String INACTIVE_CUSTOMER = "Customer inactive.";
    public static final String DELETED_CUSTOMER = "Customer deleted.";
    public static final String DATABASE_ERROR_SAVING_CUSTOMER = "Database error while saving customer";
    public static final String DATABASE_ERROR_UPDATING_CUSTOMER = "Database error while updating customer";
    public static final String DATABASE_ERROR_RETRIEVING_CUSTOMER = "Database error while retrieving customer";
    public static final String DATABASE_ERROR_CHECKING_CUSTOMER_EXISTS = "Database error while checking customer existence";
    // =========== CUSTOMER EXCEPTION MESSAGES =========== -ED

    // =========== ITEM EXCEPTION MESSAGES =========== -ST
    public static final String FAILED_TO_LOAD_ALL_ITEMS = "Failed to load all items.";
    public static final String FAILED_TO_SAVE_ITEM = "Failed to save item.";
    public static final String FAILED_TO_UPDATE_ITEM = "Failed to update item.";
    public static final String FAILED_TO_FIND_ITEM = "Failed to check if item exists.";
    public static final String FAILED_TO_UPDATE_ITEM_STATUS = "Failed to update item status.";
    public static final String FAILED_TO_GET_ITEM_STATUS = "Failed to get item status.";
    public static final String FAILED_TO_GENERATE_ITEM_CODE = "Failed to generate item code.";
    public static final String DATABASE_ERROR_SAVING_ITEM = "Database error while saving item";
    public static final String DATABASE_ERROR_UPDATING_ITEM = "Database error while updating item";
    public static final String DATABASE_ERROR_RETRIEVING_ITEM = "Database error while retrieving item";
    public static final String DATABASE_ERROR_CHECKING_ITEM_EXISTS = "Database error while checking item existence";
    public static final String DATABASE_ERROR_GENERATING_ITEM_CODE = "Database error while generating item code";
    // =========== ITEM EXCEPTION MESSAGES =========== -ED

    // =========== SUPPORT REQUEST EXCEPTION MESSAGES =========== -ST
    public static final String FAILED_TO_SAVE_SUPPORT_REQUEST = "Failed to save support request";
    public static final String FAILED_TO_RETRIEVE_SUPPORT_REQUEST = "Failed to retrieve support request";
    public static final String FAILED_TO_UPDATE_SUPPORT_REQUEST_STATUS = "Failed to update support request status";
    public static final String FAILED_TO_CHECK_TICKET_EXISTS = "Failed to check if ticket exists";
    public static final String FAILED_TO_SUBMIT_SUPPORT_REQUEST = "Failed to submit support request";
    public static final String FAILED_TO_GENERATE_TICKET_ID = "Failed to generate ticket ID";
    public static final String DATABASE_ERROR_SAVING_SUPPORT_REQUEST = "Database error while saving support request";
    public static final String DATABASE_ERROR_RETRIEVING_SUPPORT_REQUEST = "Database error while retrieving support request";
    public static final String DATABASE_ERROR_UPDATING_SUPPORT_REQUEST = "Database error while updating support request status";
    public static final String DATABASE_ERROR_CHECKING_TICKET = "Database error while checking ticket existence";
    // =========== SUPPORT REQUEST EXCEPTION MESSAGES =========== -ED

    // =========== PLACE ORDER EXCEPTION MESSAGES =========== -ST
    public static final String FAILED_TO_PLACE_ORDER = "Failed to place order";
    public static final String FAILED_TO_CREATE_ORDER = "Failed to create order";
    public static final String FAILED_TO_GENERATE_ORDER_CODE = "Failed to generate order code";
    public static final String FAILED_TO_VALIDATE_ORDER_ITEMS = "Failed to validate order items";
    public static final String FAILED_TO_CALCULATE_ORDER_TOTALS = "Failed to calculate order totals";
    public static final String FAILED_TO_UPDATE_STOCK = "Failed to update stock";
    public static final String DATABASE_ERROR_CREATING_ORDER = "Database error while creating order";
    public static final String DATABASE_ERROR_INSERTING_ORDER_ITEMS = "Database error while inserting order items";
    public static final String DATABASE_ERROR_UPDATING_STOCK = "Database error while updating stock";
    public static final String TRANSACTION_ROLLBACK_ERROR = "Transaction rollback error";
    // =========== PLACE ORDER EXCEPTION MESSAGES =========== -ED

    // =========== MANAGE ORDER EXCEPTION MESSAGES =========== -ST
    public static final String FAILED_TO_RETRIEVE_ORDERS = "Failed to retrieve orders";
    public static final String FAILED_TO_UPDATE_ORDER = "Failed to update order";
    public static final String FAILED_TO_UPDATE_ORDER_STATUS = "Failed to update order status";
    public static final String FAILED_TO_CHECK_ORDER_EXISTS = "Failed to check if order exists";
    public static final String FAILED_TO_PROCESS_ORDER_REQUEST = "Failed to process order request";
    public static final String FAILED_TO_PROCESS_STATUS_REQUEST = "Failed to process status request";
    public static final String DATABASE_ERROR_RETRIEVING_ORDERS = "Database error while retrieving orders";
    public static final String DATABASE_ERROR_UPDATING_ORDER = "Database error while updating order";
    public static final String DATABASE_ERROR_UPDATING_ORDER_STATUS = "Database error while updating order status";
    public static final String DATABASE_ERROR_CHECKING_ORDER_EXISTS = "Database error while checking order existence";
    // =========== MANAGE ORDER EXCEPTION MESSAGES =========== -ED

    // =========== DASHBOARD EXCEPTION MESSAGES =========== -ST
    public static final String FAILED_TO_LOAD_DASHBOARD_STATISTICS = "Failed to load dashboard statistics";
    public static final String FAILED_TO_LOAD_MOST_VISITED_CUSTOMERS = "Failed to load most visited customers";
    public static final String FAILED_TO_LOAD_TOP_SELLING_ITEMS = "Failed to load top selling items";
    public static final String DATABASE_ERROR_LOADING_DASHBOARD_STATS = "Database error while loading dashboard statistics";
    public static final String DATABASE_ERROR_LOADING_CUSTOMER_ANALYTICS = "Database error while loading customer analytics";
    public static final String DATABASE_ERROR_LOADING_ITEM_ANALYTICS = "Database error while loading item analytics";
    public static final String FAILED_TO_CALCULATE_DASHBOARD_METRICS = "Failed to calculate dashboard metrics";
    public static final String FAILED_TO_PROCESS_ANALYTICS_REQUEST = "Failed to process analytics request";
    // =========== DASHBOARD EXCEPTION MESSAGES =========== -ED

    // =========== REPORTS EXCEPTION MESSAGES =========== -ST
    public static final String FAILED_TO_GENERATE_ORDER_REPORTS = "Failed to generate order reports";
    public static final String FAILED_TO_GENERATE_REPORT_SUMMARY = "Failed to generate report summary";
    public static final String FAILED_TO_GENERATE_TIME_BASED_REPORTS = "Failed to generate time-based reports";
    public static final String FAILED_TO_GENERATE_DETAILED_REPORT = "Failed to generate detailed report";
    public static final String FAILED_TO_GENERATE_SUMMARY_REPORT = "Failed to generate summary report";
    public static final String FAILED_TO_VALIDATE_REPORT_FILTER = "Failed to validate report filter";
    public static final String FAILED_TO_EXECUTE_REPORT_QUERY = "Failed to execute report query";
    public static final String DATABASE_ERROR_GENERATING_REPORTS = "Database error while generating reports";
    public static final String DATABASE_ERROR_EXECUTING_REPORT_QUERY = "Database error while executing report query";
    public static final String REPORT_PROCESSING_ERROR = "Error occurred while processing report";
    public static final String INVALID_REPORT_PARAMETERS = "Invalid report parameters provided";
    // =========== REPORTS EXCEPTION MESSAGES =========== -ED

}
