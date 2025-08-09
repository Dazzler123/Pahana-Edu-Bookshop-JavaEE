package com.icbt.pahanaedubookshopjavaee.factory;

import com.icbt.pahanaedubookshopjavaee.service.*;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;

public abstract class AbstractServiceFactory {
    public abstract AbstractResponseUtility initiateAbstractUtility();
    public abstract CustomerService createCustomerService();
    public abstract ItemService createItemService();
    public abstract PlaceOrderService createPlaceOrderService();
    public abstract OrderManagementService createOrderManagementService();
    public abstract BillGenerationService createBillGenerationService();
    public abstract DashboardService createDashboardService();
    public abstract ReportsService createReportsService();
    public abstract EmailService createEmailService();
    public abstract AuthService createAuthService();
}