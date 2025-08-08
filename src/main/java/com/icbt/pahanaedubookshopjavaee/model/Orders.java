package com.icbt.pahanaedubookshopjavaee.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class Orders {
    private String orderCode;
    private String customerId;
    private BigDecimal totalAmount;
    private BigDecimal totalDiscountApplied;
    private List<OrderItem> items;
    private Date orderDate;
    private char status;
    private char paymentStatus;
    private String paymentMethod;

    public Orders() {
    }

    public Orders(String orderCode, String customerId, BigDecimal totalAmount, BigDecimal totalDiscountApplied,
                  List<OrderItem> items, Date orderDate, char status, char paymentStatus, String paymentMethod) {
        this.orderCode = orderCode;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.totalDiscountApplied = totalDiscountApplied;
        this.items = items;
        this.orderDate = orderDate;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalDiscountApplied() {
        return totalDiscountApplied;
    }

    public void setTotalDiscountApplied(BigDecimal totalDiscountApplied) {
        this.totalDiscountApplied = totalDiscountApplied;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public char getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(char paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
