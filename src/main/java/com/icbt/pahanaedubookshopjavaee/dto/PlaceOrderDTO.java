package com.icbt.pahanaedubookshopjavaee.dto;

import com.icbt.pahanaedubookshopjavaee.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public class PlaceOrderDTO {
    private String customerId;
    private BigDecimal totalAmount;
    private BigDecimal totalDiscount;
    private List<OrderItem> orderItems;
    private String paymentStatus;
    private String paymentMethod;

    public PlaceOrderDTO() {}

    public PlaceOrderDTO(String customerId, BigDecimal totalAmount, BigDecimal totalDiscount,
                        List<OrderItem> orderItems, String paymentStatus, String paymentMethod) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.totalDiscount = totalDiscount;
        this.orderItems = orderItems;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(BigDecimal totalDiscount) { this.totalDiscount = totalDiscount; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}