package com.icbt.pahanaedubookshopjavaee.model;

import java.math.BigDecimal;

public class OrderItem {
    private String itemCode;
    private int qty;
    private BigDecimal unitPrice;
    private BigDecimal discountApplied;
    private BigDecimal lineTotal;

    public OrderItem(String itemCode, int qty, BigDecimal unitPrice, BigDecimal discountApplied, BigDecimal lineTotal) {
        this.itemCode = itemCode;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.discountApplied = discountApplied;
        this.lineTotal = lineTotal;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getQty() {
        return qty;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getDiscountApplied() {
        return discountApplied;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}

