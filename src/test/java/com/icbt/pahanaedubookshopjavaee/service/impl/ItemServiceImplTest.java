package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.ItemDAO;
import com.icbt.pahanaedubookshopjavaee.model.Item;
import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.ResponseMessages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.json.JsonObject;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemServiceImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private ItemDAO itemDAO;

    private ItemServiceImpl itemService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        itemService = new ItemServiceImpl(dataSource);

        try {
            java.lang.reflect.Field daoField = ItemServiceImpl.class.getDeclaredField("itemDAO");
            daoField.setAccessible(true);
            daoField.set(itemService, itemDAO);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testGetAllItems_Success() {
        // Arrange
        List<Item> mockItems = Arrays.asList(
            new Item("ITM-001", "Book 1", new BigDecimal("25.99"), 10, 'A'),
            new Item("ITM-002", "Book 2", new BigDecimal("35.50"), 5, 'A')
        );
        when(itemDAO.findAll()).thenReturn(mockItems);

        // Act
        List<Item> result = itemService.getAllItems();

        // Assert
        assertEquals(2, result.size());
        assertEquals("ITM-001", result.get(0).getItemCode());
        verify(itemDAO).findAll();
    }

    @Test
    public void testIsItemExists_True() {
        // Arrange
        when(itemDAO.exists("ITM-001")).thenReturn(true);

        // Act
        boolean result = itemService.isItemExists("ITM-001");

        // Assert
        assertTrue(result);
        verify(itemDAO).exists("ITM-001");
    }

    @Test
    public void testIsItemExists_False() {
        // Arrange
        when(itemDAO.exists("ITM-999")).thenReturn(false);

        // Act
        boolean result = itemService.isItemExists("ITM-999");

        // Assert
        assertFalse(result);
        verify(itemDAO).exists("ITM-999");
    }

    @Test
    public void testSaveItem() {
        // Arrange
        Item item = new Item("ITM-001", "Book 1", new BigDecimal("25.99"), 10, 'A');

        // Act
        itemService.saveItem(item);

        // Assert
        verify(itemDAO).save(item);
    }

    @Test
    public void testUpdateItem() {
        // Arrange
        Item item = new Item("ITM-001", "Book 1 Updated", new BigDecimal("29.99"), 15, 'A');

        // Act
        itemService.updateItem(item);

        // Assert
        verify(itemDAO).update(item);
    }

    @Test
    public void testUpdateStatus() {
        // Arrange
        String itemCode = "ITM-001";
        char status = 'I';

        // Act
        itemService.updateStatus(itemCode, status);

        // Assert
        verify(itemDAO).updateStatus(itemCode, status);
    }

    @Test
    public void testGenerateNextItemCode() {
        // Arrange
        when(itemDAO.generateNextItemCode()).thenReturn("ITM-0005");

        // Act
        String result = itemService.generateNextItemCode();

        // Assert
        assertEquals("ITM-0005", result);
        verify(itemDAO).generateNextItemCode();
    }

    @Test
    public void testGenerateNextItemCodeAsJson_Success() {
        // Arrange
        when(itemDAO.generateNextItemCode()).thenReturn("ITM-0005");

        // Act
        JsonObject result = itemService.generateNextItemCodeAsJson();

        // Assert
        assertEquals("ITM-0005", result.getString("itemCode"));
        verify(itemDAO).generateNextItemCode();
    }

    @Test
    public void testGenerateNextItemCodeAsJson_Exception() {
        // Arrange
        when(itemDAO.generateNextItemCode()).thenThrow(new RuntimeException("Database error"));

        // Act
        JsonObject result = itemService.generateNextItemCodeAsJson();

        // Assert
        assertEquals(CommonConstants.LABEL_ERROR, result.getString(CommonConstants.LABEL_STATE));
        assertTrue(result.getString(CommonConstants.LABEL_MESSAGE).contains("Failed to generate item code"));
    }

    @Test
    public void testSaveOrUpdateItem_NewItem() {
        // Arrange
        when(itemDAO.exists("ITM-001")).thenReturn(false);

        // Act
        JsonObject result = itemService.saveOrUpdateItem("ITM-001", "Book 1", "25.99", "10", "A");

        // Assert
        assertEquals(CommonConstants.LABEL_DONE, result.getString(CommonConstants.LABEL_STATE));
        verify(itemDAO).save(any(Item.class));
        verify(itemDAO, never()).update(any(Item.class));
    }

    @Test
    public void testSaveOrUpdateItem_ExistingItem() {
        // Arrange
        when(itemDAO.exists("ITM-001")).thenReturn(true);

        // Act
        JsonObject result = itemService.saveOrUpdateItem("ITM-001", "Book 1 Updated", "29.99", "15", "A");

        // Assert
        assertEquals(CommonConstants.LABEL_DONE, result.getString(CommonConstants.LABEL_STATE));
        verify(itemDAO).update(any(Item.class));
        verify(itemDAO, never()).save(any(Item.class));
    }

    @Test
    public void testSaveOrUpdateItem_GenerateItemCode() {
        // Arrange
        when(itemDAO.generateNextItemCode()).thenReturn("ITM-001");
        when(itemDAO.exists("ITM-001")).thenReturn(false);

        // Act
        JsonObject result = itemService.saveOrUpdateItem("", "Book 1", "25.99", "10", "A");

        // Assert
        assertEquals(CommonConstants.LABEL_DONE, result.getString(CommonConstants.LABEL_STATE));
        verify(itemDAO).generateNextItemCode();
        verify(itemDAO).save(any(Item.class));
    }

    @Test
    public void testUpdateItemStatus_Success() {
        // Arrange
        when(itemDAO.exists("ITM-001")).thenReturn(true);

        // Act
        JsonObject result = itemService.updateItemStatus("ITM-001", "I");

        // Assert
        assertEquals(CommonConstants.LABEL_DONE, result.getString(CommonConstants.LABEL_STATE));
        verify(itemDAO).updateStatus("ITM-001", 'I');
    }

    @Test
    public void testUpdateItemStatus_ItemNotFound() {
        // Arrange
        when(itemDAO.exists("ITM-999")).thenReturn(false);

        // Act
        JsonObject result = itemService.updateItemStatus("ITM-999", "I");

        // Assert
        assertEquals(CommonConstants.LABEL_ERROR, result.getString(CommonConstants.LABEL_STATE));
        verify(itemDAO, never()).updateStatus(anyString(), anyChar());
    }

    @Test
    public void testGetAllItemsAsJson_Success() {
        // Arrange
        List<Item> mockItems = Arrays.asList(
            new Item("ITM-001", "Book 1", new BigDecimal("25.99"), 10, 'A')
        );
        when(itemDAO.findAll()).thenReturn(mockItems);

        // Act
        JsonObject result = itemService.getAllItemsAsJson();

        // Assert
        assertTrue(result.containsKey("items"));
        verify(itemDAO).findAll();
    }
}