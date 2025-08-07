package com.icbt.pahanaedubookshopjavaee.dto;

import java.math.BigDecimal;

public class ItemAnalyticsDTO {
    private String itemCode;
    private String name;
    private int totalSold;
    private BigDecimal totalRevenue;

    public ItemAnalyticsDTO(String itemCode, String name, int totalSold, BigDecimal totalRevenue) {
        this.itemCode = itemCode;
        this.name = name;
        this.totalSold = totalSold;
        this.totalRevenue = totalRevenue;
    }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getTotalSold() { return totalSold; }
    public void setTotalSold(int totalSold) { this.totalSold = totalSold; }
    
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}