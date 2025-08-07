package com.icbt.pahanaedubookshopjavaee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderReportDTO {
    private String orderCode;
    private String customerId;
    private String customerName;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private BigDecimal totalDiscount;
    private String status;
    private String paymentStatus;
    private int itemCount;

    public OrderReportDTO(String orderCode, String customerId, String customerName,
                          LocalDateTime orderDate, BigDecimal totalAmount, BigDecimal totalDiscount,
                          String status, String paymentStatus, int itemCount) {
        this.orderCode = orderCode;
        this.customerId = customerId;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.totalDiscount = totalDiscount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.itemCount = itemCount;
    }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(BigDecimal totalDiscount) { this.totalDiscount = totalDiscount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
}