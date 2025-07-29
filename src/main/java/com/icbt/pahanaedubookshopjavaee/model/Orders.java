package com.icbt.pahanaedubookshopjavaee.model;

import java.math.BigDecimal;
import java.util.List;

public class Orders {
    private String orderCode;
    private String customerId;
    private BigDecimal totalAmount;
    private BigDecimal totalDiscountApplied;
    private List<OrderItem> items;

    public Orders() {
    }

    public Orders(String orderCode, String customerId, BigDecimal totalAmount, BigDecimal totalDiscountApplied, List<OrderItem> items) {
        this.orderCode = orderCode;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.totalDiscountApplied = totalDiscountApplied;
        this.items = items;
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

}
