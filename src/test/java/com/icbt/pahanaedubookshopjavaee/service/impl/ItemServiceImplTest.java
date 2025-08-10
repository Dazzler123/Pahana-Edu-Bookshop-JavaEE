package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.dao.ItemDAO;
import com.icbt.pahanaedubookshopjavaee.model.Item;
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
import static org.mockito.ArgumentMatchers.*;
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
        
        // Inject mock DAO using reflection
        try {
            java.lang.reflect.Field itemDAOField = ItemServiceImpl.class.getDeclaredField("itemDAO");
            itemDAOField.setAccessible(true);
            itemDAOField.set(itemService, itemDAO);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock DAO", e);
        }
    }

    @Test
    public void testGetAllItems_ShouldReturnAllItems() {
        // Arrange
        List<Item> mockItems = Arrays.asList(
            new Item("ITM-001", "Book 1", new BigDecimal("25.99"), 10, 'A'),
            new Item("ITM-002", "Book 2", new BigDecimal("35.99"), 5, 'A')
        );
        when(itemDAO.findAll()).thenReturn(mockItems);

        // Act
        List<Item> result = itemService.getAllItems();

        // Assert
        assertEquals("Should return all items", 2, result.size());
        assertEquals("First item should match", "ITM-001", result.get(0).getItemCode());
        verify(itemDAO).findAll();
    }

    @Test
    public void testIsItemExists_ExistingItem_ShouldReturnTrue() {
        // Arrange
        when(itemDAO.exists("ITM-001")).thenReturn(true);

        // Act
        boolean result = itemService.isItemExists("ITM-001");

        // Assert
        assertTrue("Should return true for existing item", result);
        verify(itemDAO).exists("ITM-001");
    }

    @Test
    public void testIsItemExists_NonExistingItem_ShouldReturnFalse() {
        // Arrange
        when(itemDAO.exists("ITM-999")).thenReturn(false);

        // Act
        boolean result = itemService.isItemExists("ITM-999");

        // Assert
        assertFalse("Should return false for non-existing item", result);
        verify(itemDAO).exists("ITM-999");
    }

    @Test
    public void testSaveItem_ShouldCallDAOSave() {
        // Arrange
        Item item = new Item("ITM-001", "Book 1", new BigDecimal("25.99"), 10, 'A');

        // Act
        itemService.saveItem(item);

        // Assert
        verify(itemDAO).save(item);
    }

    @Test
    public void testUpdateItem_ShouldCallDAOUpdate() {
        // Arrange
        Item item = new Item("ITM-001", "Updated Book", new BigDecimal("29.99"), 15, 'A');

        // Act
        itemService.updateItem(item);

        // Assert
        verify(itemDAO).update(item);
    }

    @Test
    public void testUpdateStatus_ShouldCallDAOUpdateStatus() {
        // Act
        itemService.updateStatus("ITM-001", 'I');

        // Assert
        verify(itemDAO).updateStatus("ITM-001", 'I');
    }

    @Test
    public void testGenerateNextItemCode_ShouldReturnGeneratedCode() {
        // Arrange
        when(itemDAO.generateNextItemCode()).thenReturn("ITM-003");

        // Act
        String result = itemService.generateNextItemCode();

        // Assert
        assertEquals("Should return generated item code", "ITM-003", result);
        verify(itemDAO).generateNextItemCode();
    }

    @Test
    public void testGetAllItemsAsJson_ShouldReturnJsonObject() {
        // Arrange
        List<Item> mockItems = Arrays.asList(
            new Item("ITM-001", "Book 1", new BigDecimal("25.99"), 10, 'A')
        );
        when(itemDAO.findAll()).thenReturn(mockItems);

        // Act
        JsonObject result = itemService.getAllItemsAsJson();

        // Assert
        assertNotNull("Should return JsonObject", result);
        assertTrue("Should contain items array", result.containsKey("items"));
        verify(itemDAO).findAll();
    }

    @Test
    public void testGenerateNextItemCodeAsJson_ShouldReturnJsonObject() {
        // Arrange
        when(itemDAO.generateNextItemCode()).thenReturn("ITM-003");

        // Act
        JsonObject result = itemService.generateNextItemCodeAsJson();

        // Assert
        assertNotNull("Should return JsonObject", result);
        assertTrue("Should contain itemCode", result.containsKey("itemCode"));
        assertEquals("Should return correct item code", "ITM-003", result.getString("itemCode"));
        verify(itemDAO).generateNextItemCode();
    }
}