package com.icbt.pahanaedubookshopjavaee.dto;

import java.math.BigDecimal;

public class CustomerAnalyticsDTO {
    private String accountNumber;
    private String name;
    private int orderCount;
    private BigDecimal totalSpent;

    public CustomerAnalyticsDTO(String accountNumber, String name, int orderCount, BigDecimal totalSpent) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.orderCount = orderCount;
        this.totalSpent = totalSpent;
    }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getOrderCount() { return orderCount; }
    public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
    
    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
}