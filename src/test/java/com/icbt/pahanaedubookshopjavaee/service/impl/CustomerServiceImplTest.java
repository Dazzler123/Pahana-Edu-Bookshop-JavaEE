package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.CustomerDAO;
import com.icbt.pahanaedubookshopjavaee.model.Customer;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private CustomerDAO customerDAO;

    private CustomerServiceImpl customerService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        customerService = new CustomerServiceImpl(dataSource);

        try {
            java.lang.reflect.Field daoField = CustomerServiceImpl.class.getDeclaredField("customerDAO");
            daoField.setAccessible(true);
            daoField.set(customerService, customerDAO);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testGetAllCustomers_Success() {
        // Arrange
        List<Customer> mockCustomers = Arrays.asList(
            new Customer("CUS-001", "John Doe", "123 Main St", "1234567890", 'A'),
            new Customer("CUS-002", "Jane Smith", "456 Oak Ave", "0987654321", 'A')
        );
        when(customerDAO.findAll()).thenReturn(mockCustomers);

        // Act
        List<Customer> result = customerService.getAllCustomers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("CUS-001", result.get(0).getAccountNumber());
        verify(customerDAO).findAll();
    }

    @Test
    public void testGetAllCustomerIds_Success() {
        // Arrange
        List<String> mockIds = Arrays.asList("CUS-001", "CUS-002");
        when(customerDAO.getAllIds()).thenReturn(mockIds);

        // Act
        List<String> result = customerService.getAllCustomerIds();

        // Assert
        assertEquals(2, result.size());
        assertEquals("CUS-001", result.get(0));
        verify(customerDAO).getAllIds();
    }

    @Test
    public void testGetCustomerById_Success() {
        // Arrange
        Customer mockCustomer = new Customer("CUS-001", "John Doe", "123 Main St", "1234567890", 'A');
        when(customerDAO.getCustomer("CUS-001")).thenReturn(mockCustomer);

        // Act
        Customer result = customerService.getCustomerById("CUS-001");

        // Assert
        assertEquals("CUS-001", result.getAccountNumber());
        assertEquals("John Doe", result.getName());
        verify(customerDAO).getCustomer("CUS-001");
    }

    @Test
    public void testIsExistingCustomer_True() {
        // Arrange
        when(customerDAO.exists("CUS-001")).thenReturn(true);

        // Act
        boolean result = customerService.isExistingCustomer("CUS-001");

        // Assert
        assertTrue(result);
        verify(customerDAO).exists("CUS-001");
    }

    @Test
    public void testSaveCustomer() {
        // Arrange
        Customer customer = new Customer("CUS-001", "John Doe", "123 Main St", "1234567890", 'A');

        // Act
        customerService.saveCustomer(customer);

        // Assert
        verify(customerDAO).save(customer);
    }

    @Test
    public void testUpdateCustomer() {
        // Arrange
        Customer customer = new Customer("CUS-001", "John Doe", "123 Main St", "1234567890", 'A');

        // Act
        customerService.updateCustomer(customer);

        // Assert
        verify(customerDAO).update(customer);
    }

    @Test
    public void testGenerateNextAccountNumberAsJson_Success() {
        // Arrange
        when(customerDAO.generateNextAccountNumber()).thenReturn("CUS-0005");

        // Act
        JsonObject result = customerService.generateNextAccountNumberAsJson();

        // Assert
        assertEquals("CUS-0005", result.getString("accountNumber"));
        verify(customerDAO).generateNextAccountNumber();
    }

    @Test
    public void testGenerateNextAccountNumberAsJson_Exception() {
        // Arrange
        when(customerDAO.generateNextAccountNumber()).thenThrow(new RuntimeException("Database error"));

        // Act
        JsonObject result = customerService.generateNextAccountNumberAsJson();

        // Assert
        assertEquals(CommonConstants.LABEL_ERROR, result.getString(CommonConstants.LABEL_STATE));
        assertTrue(result.getString(CommonConstants.LABEL_MESSAGE).contains(ResponseMessages.MESSAGE_FAILED_TO_GENERATE_ACCOUNT_NUMBER));
    }

    @Test
    public void testSaveOrUpdateCustomer_NewCustomer() {
        // Arrange
        when(customerDAO.exists("CUS-001")).thenReturn(false);

        // Act
        JsonObject result = customerService.saveOrUpdateCustomer("CUS-001", "John Doe", "123 Main St", "1234567890", "A");

        // Assert
        assertEquals(CommonConstants.LABEL_DONE, result.getString(CommonConstants.LABEL_STATE));
        verify(customerDAO).save(any(Customer.class));
        verify(customerDAO, never()).update(any(Customer.class));
    }

    @Test
    public void testSaveOrUpdateCustomer_NewCustomer_GenerateAccountNumber() {
        // Arrange
        when(customerDAO.generateNextAccountNumber()).thenReturn("CUS-001");
        when(customerDAO.exists("CUS-001")).thenReturn(false);

        // Act
        JsonObject result = customerService.saveOrUpdateCustomer("", "John Doe", "123 Main St", "1234567890", "A");

        // Assert
        assertEquals(CommonConstants.LABEL_DONE, result.getString(CommonConstants.LABEL_STATE));
        verify(customerDAO).generateNextAccountNumber();
        verify(customerDAO).save(any(Customer.class));
        verify(customerDAO, never()).update(any(Customer.class));
    }

    @Test
    public void testSaveOrUpdateCustomer_ExistingCustomer() {
        // Arrange
        when(customerDAO.exists("CUS-001")).thenReturn(true);

        // Act
        JsonObject result = customerService.saveOrUpdateCustomer("CUS-001", "John Doe Updated", "123 Main St", "1234567890", "A");

        // Assert
        assertEquals(CommonConstants.LABEL_DONE, result.getString(CommonConstants.LABEL_STATE));
        verify(customerDAO).update(any(Customer.class));
        verify(customerDAO, never()).save(any(Customer.class));
    }

    @Test
    public void testSaveOrUpdateCustomer_ValidationError_EmptyName() {
        // Act
        JsonObject result = customerService.saveOrUpdateCustomer("CUS-001", "", "123 Main St", "1234567890", "A");

        // Assert
        assertEquals(CommonConstants.LABEL_ERROR, result.getString(CommonConstants.LABEL_STATE));
        assertEquals(ResponseMessages.MESSAGE_CUSTOMER_NAME_REQUIRED, result.getString(CommonConstants.LABEL_MESSAGE));
        verify(customerDAO, never()).save(any(Customer.class));
        verify(customerDAO, never()).update(any(Customer.class));
    }

    @Test
    public void testUpdateCustomerStatus_Success() {
        // Arrange
        when(customerDAO.exists("CUS-001")).thenReturn(true);

        // Act
        JsonObject result = customerService.updateCustomerStatus("CUS-001", "I");

        // Assert
        assertEquals(CommonConstants.LABEL_DONE, result.getString(CommonConstants.LABEL_STATE));
        verify(customerDAO).updateStatus("CUS-001", 'I');
    }

    @Test
    public void testUpdateCustomerStatus_CustomerNotFound() {
        // Arrange
        when(customerDAO.exists("CUS-999")).thenReturn(false);

        // Act
        JsonObject result = customerService.updateCustomerStatus("CUS-999", "I");

        // Assert
        assertEquals(CommonConstants.LABEL_ERROR, result.getString(CommonConstants.LABEL_STATE));
        assertEquals(ResponseMessages.MESSAGE_CUSTOMER_NOT_FOUND, result.getString(CommonConstants.LABEL_MESSAGE));
        verify(customerDAO, never()).updateStatus(anyString(), anyChar());
    }

    @Test
    public void testGetAllCustomersAsJson_Success() {
        // Arrange
        List<Customer> mockCustomers = Arrays.asList(
            new Customer("CUS-001", "John Doe", "123 Main St", "1234567890", 'A')
        );
        when(customerDAO.findAll()).thenReturn(mockCustomers);

        // Act
        JsonObject result = customerService.getAllCustomersAsJson();

        // Assert
        assertTrue(result.containsKey("customers"));
        verify(customerDAO).findAll();
    }

    @Test
    public void testGetCustomerByAccountNumberAsJson_Success() {
        // Arrange
        Customer mockCustomer = new Customer("CUS-001", "John Doe", "123 Main St", "1234567890", 'A');
        when(customerDAO.getCustomer("CUS-001")).thenReturn(mockCustomer);

        // Act
        JsonObject result = customerService.getCustomerByAccountNumberAsJson("CUS-001");

        // Assert
        assertEquals("CUS-001", result.getString("accountNumber"));
        assertEquals("John Doe", result.getString("name"));
        verify(customerDAO).getCustomer("CUS-001");
    }

    @Test
    public void testGetCustomerByAccountNumberAsJson_NotFound() {
        // Arrange
        when(customerDAO.getCustomer("CUS-999")).thenReturn(null);

        // Act
        JsonObject result = customerService.getCustomerByAccountNumberAsJson("CUS-999");

        // Assert
        assertEquals(CommonConstants.LABEL_ERROR, result.getString(CommonConstants.LABEL_STATE));
        assertEquals("Customer not found", result.getString(CommonConstants.LABEL_MESSAGE));
    }

}