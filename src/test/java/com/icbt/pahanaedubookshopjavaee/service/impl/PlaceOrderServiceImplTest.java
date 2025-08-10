package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.PlaceOrderDAO;
import com.icbt.pahanaedubookshopjavaee.dto.PlaceOrderDTO;
import com.icbt.pahanaedubookshopjavaee.model.OrderItem;
import com.icbt.pahanaedubookshopjavaee.util.constants.ExceptionMessages;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants.REPLACER;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlaceOrderServiceImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private PlaceOrderDAO placeOrderDAO;

    private PlaceOrderServiceImpl placeOrderService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        placeOrderService = new PlaceOrderServiceImpl(dataSource);

        try {
            java.lang.reflect.Field daoField = PlaceOrderServiceImpl.class.getDeclaredField("placeOrderDAO");
            daoField.setAccessible(true);
            daoField.set(placeOrderService, placeOrderDAO);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testPlaceOrder_Success() throws Exception {
        // Arrange
        List<OrderItem> orderItems = Arrays.asList(
            new OrderItem("ITM-001", 2, new BigDecimal("25.99"), new BigDecimal("0.00"), new BigDecimal("51.98"))
        );
        PlaceOrderDTO placeOrderDTO = new PlaceOrderDTO(
            "CUS-001",
            new BigDecimal("51.98"),
            new BigDecimal("0.00"),
            orderItems,
            "Paid",
            CommonConstants.PAYMENT_METHOD_CASH
        );
        when(placeOrderDAO.createOrder(anyString(), any(BigDecimal.class), any(BigDecimal.class),
                                     anyList(), anyString(), anyString())).thenReturn("ORD-001");

        // Act
        String result = placeOrderService.placeOrder(placeOrderDTO);

        // Assert
        assertEquals("ORD-001", result);
        verify(placeOrderDAO).createOrder("CUS-001", new BigDecimal("51.98"), new BigDecimal("0.00"),
                                         orderItems, "Paid", CommonConstants.PAYMENT_METHOD_CASH);
    }

    @Test
    public void testProcessOrderRequest_Success() throws Exception {
        // Arrange
        JsonArray itemsArray = Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add("itemCode", "ITM-001")
                .add("qty", 2)
                .add("unitPrice", 25.99)
                .add("discount", 0.00))
            .build();

        JsonObject orderRequest = Json.createObjectBuilder()
            .add("customerAccount", "CUS-001")
            .add("paymentMethod", CommonConstants.PAYMENT_METHOD_CASH)
            .add("items", itemsArray)
            .build();

        when(placeOrderDAO.createOrder(anyString(), any(BigDecimal.class), any(BigDecimal.class),
                                     anyList(), anyString(), anyString())).thenReturn("ORD-001");

        // Act
        JsonObject result = placeOrderService.processOrderRequest(orderRequest);

        // Assert
        assertEquals("done", result.getString("state"));
        assertEquals("ORD-001", result.getString("orderCode"));
    }

    @Test
    public void testProcessOrderRequest_MissingCustomerAccount() {
        // Arrange
        JsonObject orderRequest = Json.createObjectBuilder()
            .add("paymentMethod", CommonConstants.PAYMENT_METHOD_CASH)
            .add("items", Json.createArrayBuilder().build())
            .build();

        // Act
        JsonObject result = placeOrderService.processOrderRequest(orderRequest);

        // Assert
        assertEquals("error", result.getString("state"));
        assertEquals(ResponseMessages.MESSAGE_CUSTOMER_ACCOUNT_REQUIRED, result.getString("message"));
    }

    @Test
    public void testProcessOrderRequest_EmptyItems() {
        // Arrange
        JsonObject orderRequest = Json.createObjectBuilder()
            .add("customerAccount", "CUS-001")
            .add("paymentMethod", CommonConstants.PAYMENT_METHOD_CASH)
            .add("items", Json.createArrayBuilder().build())
            .build();

        // Act
        JsonObject result = placeOrderService.processOrderRequest(orderRequest);

        // Assert
        assertEquals("error", result.getString("state"));
        assertEquals(ResponseMessages.MESSAGE_ORDER_ITEMS_REQUIRED, result.getString("message"));
    }

    @Test
    public void testProcessOrderRequest_ZeroTotalAmount() {
        // Arrange
        JsonArray itemsArray = Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add("itemCode", "ITM-001")
                .add("qty", 2)
                .add("unitPrice", 0.00)
                .add("discount", 0.00))
            .build();

        JsonObject orderRequest = Json.createObjectBuilder()
            .add("customerAccount", "CUS-001")
            .add("paymentMethod", CommonConstants.PAYMENT_METHOD_CASH)
            .add("items", itemsArray)
            .build();

        // Act
        JsonObject result = placeOrderService.processOrderRequest(orderRequest);

        // Assert
        assertEquals("error", result.getString("state"));
        assertEquals(ResponseMessages.MESSAGE_UNIT_PRICE_MUST_BE_POSITIVE_FOR_ORDER.replace(REPLACER, "ITM-001"), result.getString("message"));
    }

    @Test
    public void testProcessOrderRequest_InvalidItemData() {
        // Arrange
        JsonArray itemsArray = Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add("itemCode", "")
                .add("qty", 2)
                .add("unitPrice", "25.99")
                .add("discount", "0.00"))
            .build();

        JsonObject orderRequest = Json.createObjectBuilder()
            .add("customerAccount", "CUS-001")
            .add("paymentMethod", CommonConstants.PAYMENT_METHOD_CASH)
            .add("items", itemsArray)
            .build();

        // Act
        JsonObject result = placeOrderService.processOrderRequest(orderRequest);

        // Assert
        assertEquals("error", result.getString("state"));
        assertEquals(ResponseMessages.MESSAGE_ITEM_CODE_REQUIRED_FOR_ORDER, result.getString("message"));
    }

    @Test
    public void testProcessOrderRequest_InvalidQuantity() {
        // Arrange
        JsonArray itemsArray = Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add("itemCode", "ITM-001")
                .add("qty", 0)
                .add("unitPrice", "25.99")
                .add("discount", "0.00"))
            .build();

        JsonObject orderRequest = Json.createObjectBuilder()
            .add("customerAccount", "CUS-001")
            .add("paymentMethod", CommonConstants.PAYMENT_METHOD_CASH)
            .add("items", itemsArray)
            .build();

        // Act
        JsonObject result = placeOrderService.processOrderRequest(orderRequest);

        // Assert
        assertEquals("error", result.getString("state"));
        assertEquals(ResponseMessages.MESSAGE_QUANTITY_MUST_BE_POSITIVE.replace(REPLACER, "ITM-001"), result.getString("message"));
    }

    @Test
    public void testProcessOrderRequest_DatabaseException() throws Exception {
        // Arrange
        JsonArray itemsArray = Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add("itemCode", "ITM-001")
                .add("qty", 2)
                .add("unitPrice", 25.99)
                .add("discount", 0.00))
            .build();

        JsonObject orderRequest = Json.createObjectBuilder()
            .add("customerAccount", "CUS-001")
            .add("paymentMethod", CommonConstants.PAYMENT_METHOD_CASH)
            .add("items", itemsArray)
            .build();

        when(placeOrderDAO.createOrder(anyString(), any(BigDecimal.class), any(BigDecimal.class),
                                     anyList(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        JsonObject result = placeOrderService.processOrderRequest(orderRequest);

        // Assert
        assertEquals("error", result.getString("state"));
        assertTrue(result.getString("message").contains(ExceptionMessages.FAILED_TO_PLACE_ORDER));
        assertTrue(result.getString("message").contains("Database connection failed"));
    }

    @Test
    public void testProcessOrderRequest_ExcessiveDiscount() {
        // Arrange
        JsonArray itemsArray = Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add("itemCode", "ITM-001")
                .add("qty", 2)
                .add("unitPrice", 25.99)
                .add("discount", 150.00))
            .build();

        JsonObject orderRequest = Json.createObjectBuilder()
            .add("customerAccount", "CUS-001")
            .add("paymentMethod", CommonConstants.PAYMENT_METHOD_CASH)
            .add("items", itemsArray)
            .build();

        // Act
        JsonObject result = placeOrderService.processOrderRequest(orderRequest);

        // Assert
        assertEquals("error", result.getString("state"));
        assertTrue(result.getString("message").contains(ResponseMessages.MESSAGE_DISCOUNT_PERCENTAGE_INVALID.replace(REPLACER, "ITM-001")));
    }

}
