package com.icbt.pahanaedubookshopjavaee.factory;

import com.icbt.pahanaedubookshopjavaee.service.*;
import com.icbt.pahanaedubookshopjavaee.service.impl.*;
import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;

import javax.sql.DataSource;

/**
 * This class is the implementation of Factory and Abstract Factory Patterns
 * This class is used to create the service objects
 *
 */
public class ServiceFactory extends AbstractServiceFactory {
    private static ServiceFactory instance;
    private final DataSource dataSource;
    
    private ServiceFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This method is used to get the unique, one-and-only instance of the ServiceFactory
     *
     * @param dataSource
     * @return
     */
    public static ServiceFactory getInstance(DataSource dataSource) {
        if (instance == null) {
            synchronized (ServiceFactory.class) {
                if (instance == null) {
                    instance = new ServiceFactory(dataSource);
                }
            }
        }
        return instance;
    }

    @Override
    public AbstractResponseUtility initiateAbstractUtility() {
        return new AbstractResponseUtility();
    }

    @Override
    public CustomerService createCustomerService() {
        return new CustomerServiceImpl(dataSource);
    }
    
    @Override
    public ItemService createItemService() {
        return new ItemServiceImpl(dataSource);
    }
    
    @Override
    public DashboardService createDashboardService() {
        return new DashboardServiceImpl(dataSource);
    }
    
    @Override
    public PlaceOrderService createPlaceOrderService() {
        return new PlaceOrderServiceImpl(dataSource);
    }

    @Override
    public OrderManagementService createOrderManagementService() {
        return new OrderManagementServiceImpl(dataSource);
    }

    @Override
    public ReportsService createReportsService() {
        return new ReportsServiceImpl(dataSource);
    }

    @Override
    public EmailService createEmailService() {
        return new EmailServiceImpl();
    }

    @Override
    public AuthService createAuthService() {
        return new AuthServiceImpl();
    }

    @Override
    public BillGenerationService createBillGenerationService() {
        return new BillGenerationServiceImpl(dataSource);
    }

    @Override
    public SupportRequestService createSupportRequestService() {
        EmailService emailService = createEmailService();
        return new SupportRequestServiceImpl(dataSource, emailService);
    }

}
